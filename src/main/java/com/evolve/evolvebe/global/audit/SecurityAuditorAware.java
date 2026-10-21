package com.evolve.evolvebe.global.audit;

import com.evolve.evolvebe.auth.domain.AuthenticationProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * JWT 기반 현재 사용자 ID 자동 감지 (Spring Data JPA AuditorAware)
 */
@Component("auditorAware")
public class SecurityAuditorAware implements AuditorAware<String> {

    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public SecurityAuditorAware(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            // 현재 HTTP 요청 객체 가져오기
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
                return Optional.of("SYSTEM");
            }

            HttpServletRequest request = servletAttrs.getRequest();
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                return Optional.of("SYSTEM");
            }

            // JWT 토큰에서 사용자 ID 추출
            String accessToken = authenticationProvider.extractToken(authorizationHeader);
            String userId = authenticationProvider.extractUserId(accessToken);

            return Optional.ofNullable(userId).filter(id -> !id.isBlank()).or(() -> Optional.of("SYSTEM"));
        } catch (Exception e) {
            // 요청 스코프 외 (비동기/배치 등)
            return Optional.of("SYSTEM");
        }
    }
}
