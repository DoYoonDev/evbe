package com.evolve.evolvebe.global.log;

import com.evolve.evolvebe.log.application.LogFacade;
import com.evolve.evolvebe.log.domain.LogCommand;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginLoggingAspect {

    private final LogFacade logFacade;

    @Pointcut("execution(* com.evolve.evolvebe.auth.interfaces.AuthController.login(..))")
    public void loginMethod() {}

    @Around("loginMethod()")
    public Object logLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attrs.getRequest();

        String ip = clientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String userId = extractUserId(joinPoint, request);

        Object result = null;
        Exception exception = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            LogCommand.CreateLogLogin createLogLogin = LogCommand.CreateLogLogin.builder()
                    .memberId(userId)
                    .accessIp(ip)
                    .userAgent(userAgent)
                    .success(exception == null)
                    .build();

            logFacade.createLogLogin(createLogLogin);

            log.debug("Login log recorded: userId={}, ip={}, success={}", userId, ip, exception == null);
        }
    }

    private String extractUserId(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
        try {
            // 1) DTO (AuthCommand.Login) 검사
            for (Object arg : joinPoint.getArgs()) {
                if (arg == null) continue;
                try {
                    var method = arg.getClass().getMethod("getUserId");
                    Object val = method.invoke(arg);
                    if (val != null && !val.toString().isBlank()) return val.toString();
                } catch (NoSuchMethodException ignore) {}
            }

            // 2) Map 형태 검사
            return Arrays.stream(joinPoint.getArgs())
                    .filter(arg -> arg instanceof java.util.Map<?, ?>)
                    .map(arg -> ((java.util.Map<?, ?>) arg).get("userId"))
                    .filter(v -> v != null && !v.toString().isBlank())
                    .map(Object::toString)
                    .findFirst()
                    .orElseGet(() -> {
                        // 3) request 파라미터
                        String p = request.getParameter("userId");
                        return (p != null && !p.isBlank()) ? p : "[unknown]";
                    });
        } catch (Exception e) {
            log.warn("Failed to extract userId: {}", e.getMessage());
            return "[unknown]";
        }
    }

    private String clientIp(HttpServletRequest req) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "CF-Connecting-IP", "X-Client-IP"};
        for (String h : headers) {
            String v = req.getHeader(h);
            if (v != null && !v.isBlank()) {
                return v.split(",")[0].trim();
            }
        }
        return req.getRemoteAddr();
    }

}
