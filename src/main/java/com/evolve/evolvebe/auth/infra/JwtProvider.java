package com.evolve.evolvebe.auth.infra;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

	private final SecretKey secretKey;
	private final Duration accessExpirationTime;
	private final Duration refreshExpirationTime;
    private final Clock clock = Clock.systemUTC();

    public JwtProvider(
            @Value("${security.jwt.secret-key}") String secretProp,
            @Value("${security.jwt.access-expiration-time}") Duration accessExpirationTime,
            @Value("${security.jwt.refresh-expiration-time}") Duration refreshExpirationTime
    ) {
        this.secretKey = buildKey(secretProp);   // 안전 키 생성
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    /* ===== Creation ===== */

    public String generateAccessToken(final String subject) {
        return createToken(subject, accessExpirationTime, "ACCESS");
    }

    public String generateRefreshToken(final String subject) {
        return createToken(subject, refreshExpirationTime, "REFRESH");
    }

    private String createToken(final String subject, final Duration ttl, final String typ) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("subject must not be blank");
        }
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("ttl must be positive");
        }

        final Instant now = clock.instant();
        final Instant exp = now.plus(ttl);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .notBefore(Date.from(now))
                .subject(subject)
                .expiration(Date.from(exp))
                .claim("typ", typ) // 토큰 타입 명시
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /* ===== Parse / Validate ===== */

    /** Access 토큰 보증: 만료→ExpiredJwtException, 기타→JwtException */
    public void ensureValidAccessToken(final String token) {
        ensureValidTokenType(parseToken(token), "ACCESS");
    }

    /** Refresh 토큰 보증: 만료→ExpiredJwtException, 기타→JwtException */
    public void ensureValidRefreshToken(final String token) {
        ensureValidTokenType(parseToken(token), "REFRESH");
    }

    public boolean validateToken(final String token) {
        try {
            parseToken(token);
            return true;
        } catch (final JwtException | IllegalArgumentException e) {
            return false;
        }
    }

	public String getSubject(final String token) {
		return parseToken(token)
			.getPayload()
			.getSubject();
	}

    /** 만료된 토큰에서도(서명 검증됨) subject 추출 */
    public String getSubjectAllowExpired(final String token) {
        try {
            return getSubject(token); // 만료 X
        } catch (ExpiredJwtException ex) { // 만료 O (서명 검증 후)
            return ex.getClaims() != null ? ex.getClaims().getSubject() : null;
        } catch (JwtException | IllegalArgumentException e) { // 서명 불일치/형식 오류 등
            return null;
        }
    }

	private Jws<Claims> parseToken(final String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.clockSkewSeconds(60)
			.build()
			.parseSignedClaims(token);
	}

    private static void ensureValidTokenType(Jws<Claims> jws, String expected) {
        String typ = jws.getPayload().get("typ", String.class);
        if (typ != null && !expected.equals(typ)) {
            throw new JwtException("Invalid token type: expected " + expected + " but was " + typ);
        }
    }

    /* ==========================
     * Helpers for Filter/Controller
     * ========================== */

    /** "Bearer xxx" 형식의 Authorization 헤더에서 액세스 토큰만 추출 */
    public String extractAccessToken(final String authorizationHeader) {
        return cleanBearerHeader(authorizationHeader);
    }

    /** 쿠키/헤더 등 혼합 입력에서 토큰 정규화 */
    public String normalizeIncomingToken(String cookieValue, String headerValue) {
        String candidate = (headerValue != null && !headerValue.isBlank()) ? headerValue : cookieValue;
        return cleanToken(candidate);
    }

    /** ms 단위 TTL 조회 (프론트 전달용) */
    public long getAccessTtlMillis() {
        return accessExpirationTime.toMillis();
    }

    public long getRefreshTtlMillis() {
        return refreshExpirationTime.toMillis();
    }

    /* ===== Key builder ===== */

    private static SecretKey buildKey(String secretProp) {
        try {
            byte[] decoded = io.jsonwebtoken.io.Decoders.BASE64.decode(secretProp);
            if (decoded.length < 32) throw new IllegalArgumentException("Base64 key too short");
            return io.jsonwebtoken.security.Keys.hmacShaKeyFor(decoded);
        } catch (Exception ignore) {
            byte[] raw = secretProp.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (raw.length < 32) {
                throw new IllegalArgumentException("JWT secret too short (need >= 256 bits)");
            }
            return io.jsonwebtoken.security.Keys.hmacShaKeyFor(raw);
        }
    }

    /* ===== utils ===== */

    private static String cleanToken(String val) {
        if (val == null) return null;
        String token = val.trim();
        if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }
        return token.isEmpty() ? null : token;
    }

    private static String cleanBearerHeader(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) return null;
        if (!authorizationHeader.regionMatches(true, 0, "Bearer ", 0, 7)) return null;
        String token = authorizationHeader.substring(7).trim();
        return token.isEmpty() ? null : token;
    }
}