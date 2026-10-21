package com.evolve.evolvebe.global.log;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.evolve.evolvebe.global.util.SecurityUtil;
import com.evolve.evolvebe.log.application.LogFacade;
import com.evolve.evolvebe.log.domain.LogCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
@AllArgsConstructor
public class LoggingAspect {

	private final LogFacade logFacade;
	private final ObjectMapper objectMapper;

	// ===== 민감정보 마스킹 키/패턴 =====
	private static final Set<String> SENSITIVE_KEYS = new HashSet<>(Arrays.asList(
			"password", "pwd", "newPassword", "oldPassword", "confirmPassword",
			"authorization", "accessToken", "refreshToken", "idToken", "token",
			"secret", "clientSecret", "apiKey", "cookie", "set-cookie"
	));

	private static final List<Pattern> SENSITIVE_KEY_PATTERNS = Arrays.asList(
			Pattern.compile(".*pass(word)?$", Pattern.CASE_INSENSITIVE),
			Pattern.compile(".*token$", Pattern.CASE_INSENSITIVE),
			Pattern.compile("authorization", Pattern.CASE_INSENSITIVE),
			Pattern.compile(".*secret$", Pattern.CASE_INSENSITIVE),
			Pattern.compile("set-cookie", Pattern.CASE_INSENSITIVE),
			Pattern.compile("cookie", Pattern.CASE_INSENSITIVE)
	);

	private static final String MASK = "******";
	private static final int MAX_JSON_LEN = 10 * 1024;      // 요청 파라미터 기본 10KB 제한(기존 로직 유지)
	private static final int MAX_INFO_BYTES  = 10 * 1024;   // 2xx~3xx 응답 바디 최대 저장 10KB
	private static final int MAX_ERROR_BYTES = 30 * 1024;

	// com.evolve.evolvebe 패키지와 그 하위 패키지 모든 Controller 클래스의 모든 메서드를 대상
	@Pointcut("execution(* com.evolve.evolvebe..*Controller.*(..))")
	public void controller() {}

	// Around 어드바이스: 메서드 실행 전후로 로깅 수행
	@Around("controller()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		ServletRequestAttributes attrs =
				(ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attrs.getRequest();

		String className = joinPoint.getSignature().getDeclaringTypeName();
		String controllerMethod = joinPoint.getSignature().getName();
		Object[] methodArgs = joinPoint.getArgs();

		String url = request.getRequestURL().toString();
		String ip = clientIp(request);
		String httpMethod = request.getMethod();
		String userAgent = safeUserAgent(request.getHeader("User-Agent"));

		// 인자 → 안전 JSON
		String params = convertArgsToSafeJson(methodArgs);

		log.debug("Enter {}.{}() httpMethod={} url={} ip={} ua={}",
				className, controllerMethod, httpMethod, url, ip, userAgent);

		String userId = SecurityUtil.getCurrentUser()
				.map(UserDetails::getUsername)
				.orElse("");

		LogCommand.CreateLogAccess createLogAccess = LogCommand.CreateLogAccess.builder()
				.memberId(userId)
				.requestUrl(url)
				.requestMethod(httpMethod) // 컨트롤러 메서드명이 아니라 HTTP 메서드
				.requestPayload(params)
				.requestIp(ip)
				.userAgent(userAgent)
				.build();

		Object result = null;
		try {
			result = joinPoint.proceed();

			if (result instanceof ResponseEntity<?> responseEntity) {
				int status = responseEntity.getStatusCode().value();
				createLogAccess.setResponseStatus(status);

				Object body = responseEntity.getBody();
				// ▼ 변경: 응답 Content-Type 확인
				String contentType = Optional.ofNullable(responseEntity.getHeaders().getContentType())
						.map(MediaType::toString).orElse(null);

				// 마스킹 → 문자열화
				String bodyJson = convertValueToSafeJson(body);
				// 길이 제한/해시/바이너리 스킵 포함 요약
				String summarized = summarizeBody(bodyJson, contentType, status);
				createLogAccess.setResponseBody(summarized);;
			} else {
				String bodyJson = convertValueToSafeJson(result);
				String summarized = summarizeBody(bodyJson, null, 200);
				createLogAccess.setResponseBody(summarized);
			}

			return result;
		} catch (Exception e) {
			String summarized = summarizeBody("Exception: " + e.getMessage(), "text/plain", 500);
			createLogAccess.setResponseBody(summarized);
			throw e;
		} finally {
			logFacade.createLogAccess(createLogAccess);
		}
	}

	// ====== 유틸 ======

	// Args에서 위험 타입 제외 + 재귀 마스킹 후 JSON
	private String convertArgsToSafeJson(Object[] args) {
		try {
			List<Object> safeArgs = Arrays.stream(args)
					.filter(this::isLoggableArg)
					.map(this::toRedactedValue)
					.collect(Collectors.toList());

			String json = objectMapper.writeValueAsString(safeArgs);
			return truncate(json, MAX_JSON_LEN);
		} catch (Exception e) {
			log.warn("Failed to convert args to JSON: {}", e.getMessage());
			return "[args_unserializable]";
		}
	}

	private String convertValueToSafeJson(Object value) {
		try {
			Object safe = toRedactedValue(value);
			return objectMapper.writeValueAsString(safe);
		} catch (JsonProcessingException e) {
			log.warn("Failed to convert response to JSON: {}", e.getMessage());
			return value != null ? String.valueOf(value) : "[null]";
		}
	}

	@SuppressWarnings("unchecked")
	private Object toRedactedValue(Object v) {
		if (v == null) return null;

		// 위험/대용량 타입은 요약만
		if (v instanceof HttpServletRequest) return "[HttpServletRequest]";
		if (v instanceof HttpServletResponse) return "[HttpServletResponse]";
		if (v instanceof MultipartFile mf)
			return Map.of("file", Map.of(
					"name", mf.getOriginalFilename() != null ? mf.getOriginalFilename() : "",
					"size", mf.getSize(),
					"contentType", mf.getContentType() != null ? mf.getContentType() : ""
			));
		// Spring의 BindingResult 등은 제외
		if (v.getClass().getName().contains("BindingResult")) return "[BindingResult]";

		if (v instanceof CharSequence) return v; // 문자열은 키 문맥이 없으므로 그대로(상위 Map에서 마스킹)

		if (v instanceof Map<?, ?> m) {
			Map<String, Object> out = new LinkedHashMap<>();
			for (Map.Entry<?, ?> e : m.entrySet()) {
				String key = String.valueOf(e.getKey());
				Object value = e.getValue();
				out.put(key, redactByKey(key, value));
			}
			return out;
		}

		if (v instanceof Collection<?> c) {
			List<Object> out = new ArrayList<>(c.size());
			for (Object elem : c) out.add(toRedactedValue(elem));
			return out;
		}

		if (v.getClass().isArray()) {
			int len = java.lang.reflect.Array.getLength(v);
			List<Object> out = new ArrayList<>(len);
			for (int i = 0; i < len; i++)
				out.add(toRedactedValue(java.lang.reflect.Array.get(v, i)));
			return out;
		}

		// DTO/Record 등 → Map으로 변환 후 키 기반 마스킹
		try {
			Map<String, Object> asMap = objectMapper.convertValue(v, Map.class);
			return toRedactedValue(asMap);
		} catch (IllegalArgumentException ignore) {
			// 변환 불가 → toString 제한
			return truncate(String.valueOf(v), 1024);
		}
	}

	private Object redactByKey(String key, Object value) {
		if (isSensitiveKey(key)) return MASK;

		// Authorization 헤더처럼 토큰이 문자열로 오는 경우 일부만 마스킹
		if (value instanceof CharSequence s && looksLikeTokenKey(key)) {
			return maskTokenLike(String.valueOf(s));
		}
		return toRedactedValue(value);
	}

	private boolean isSensitiveKey(String key) {
		String k = key == null ? "" : key.trim();
		if (SENSITIVE_KEYS.contains(k)) return true;
		for (Pattern p : SENSITIVE_KEY_PATTERNS) {
			if (p.matcher(k).matches()) return true;
		}
		return false;
	}

	private boolean looksLikeTokenKey(String key) {
		String k = key == null ? "" : key.toLowerCase(Locale.ROOT);
		return k.contains("token") || k.contains("authorization") || k.contains("cookie");
	}

	private String maskTokenLike(String raw) {
		if (raw == null || raw.isBlank()) return MASK;
		// Bearer 토큰 형태 지원
		if (raw.toLowerCase().startsWith("bearer ")) {
			String val = raw.substring(7);
			return "Bearer " + partialMask(val);
		}
		return partialMask(raw);
	}

	private String partialMask(String s) {
		if (s.length() <= 8) return MASK;
		String head = s.substring(0, 4);
		String tail = s.substring(s.length() - 4);
		return head + "****" + tail;
	}

	private boolean isLoggableArg(Object v) {
		if (v == null) return true;

		return !(v instanceof HttpServletRequest
				|| v instanceof HttpServletResponse
				|| v instanceof MultipartFile
				|| v.getClass().getName().contains("BindingResult"));
	}

	private String clientIp(HttpServletRequest req) {
		String[] headers = {
				"X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP", "X-Client-IP"
		};
		for (String h : headers) {
			String v = req.getHeader(h);
			if (v != null && !v.isBlank()) {
				// XFF는 "client, proxy1, proxy2" 형태 → 첫번째
				return v.split(",")[0].trim();
			}
		}
		return req.getRemoteAddr();
	}

	private String safeUserAgent(String ua) {
		if (ua == null) return "";
		// UA에 쿠키/토큰이 실릴 일은 거의 없지만, 방어적으로 토큰류 패턴 마스킹
		return looksLikeTokenKey("user-agent") ? maskTokenLike(ua) : ua;
	}

	private String truncate(String s, int max) {
		if (s == null) return null;
		if (s.length() <= max) return s;
		return s.substring(0, max) + "...[truncated]";
	}

	private String summarizeBody(String body, String contentType, int status) {
		if (body == null) return "[null]";
		if (isBinaryContentType(contentType)) return "[binary skipped]";

		int limit = isErrorStatus(status) ? MAX_ERROR_BYTES : MAX_INFO_BYTES;
		boolean truncated = body.length() > limit;

		String snippet = truncated ? body.substring(0, limit) : body;

		// JSON이면 가독성 좋게 (실패해도 그대로 둠)
		if (looksLikeJson(snippet, contentType)) {
			try {
				var node = objectMapper.readTree(snippet);
				snippet = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
			} catch (Exception ignore) { /* 그대로 유지 */ }
		}

		return truncated ? snippet + "...[truncated]" : snippet;
	}

	private boolean isErrorStatus(int status) {
		return status >= 400;
	}

	private boolean isBinaryContentType(String ct) {
		if (ct == null) return false;
		String c = ct.toLowerCase(Locale.ROOT);
		return c.startsWith("application/octet-stream")
				|| c.startsWith("application/pdf")
				|| c.startsWith("application/zip")
				|| c.startsWith("application/vnd")   // 엑셀/워드 등
				|| c.startsWith("image/")
				|| c.startsWith("audio/")
				|| c.startsWith("video/");
	}

	private boolean looksLikeJson(String text, String contentType) {
		if (contentType != null && contentType.toLowerCase(Locale.ROOT).contains("json")) return true;
		if (text == null) return false;
		String t = text.trim();
		return (t.startsWith("{") && t.endsWith("}")) || (t.startsWith("[") && t.endsWith("]"));
	}

}
