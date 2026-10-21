package com.evolve.evolvebe.auth.domain;

import java.time.Duration;
import java.util.List;

import com.evolve.evolvebe.auth.infra.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.role.domain.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationProvider authenticationProvider;
	private final RoleService roleService;
	private final JwtProvider jwtProvider;

	@Value("${security.jwt.refresh-expiration-time}")
	private Duration refreshTtl;

	public void authenticateMember(AuthCommand.Login command) {
		try {
			authenticationProvider.authenticate(command);
		} catch (AuthenticationException e) {
			throw new CustomException(ErrorCode.USER_OR_PASSWORD_NOT_MATCH);
		}
	}

	public Token.MemberToken generateLoginToken(String userId) {
		String accessToken = authenticationProvider.generateAccessToken(userId);
		String refreshToken = authenticationProvider.generateRefreshToken(userId);

		return Token.MemberToken.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public void saveRefreshToken(String userId, String refreshToken) {
		authenticationProvider.saveRefreshToken(userId, refreshToken);
	}

	public ResponseCookie generateCookie(String refreshToken) {
		return ResponseCookie.from("refresh-token", refreshToken)
			.maxAge(refreshTtl)
			.sameSite("Lax") // https 사용시 None
			.httpOnly(true)
			.path("/")
			.build();
	}

	public Token.MemberToken refreshAccessToken(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		refreshToken = jwtProvider.normalizeIncomingToken(null, refreshToken);
		String userId = authenticationProvider.extractUserId(refreshToken);

		String newTokens = authenticationProvider.regenerateToken(refreshToken);
		authenticationProvider.saveRefreshToken(userId, newTokens);

		return buildMemberToken(newTokens);
	}

	private Token.MemberToken buildMemberToken(String accessToken) {
		return Token.MemberToken.builder()
			.accessToken(accessToken)
			.build();
	}

	public AuthInfo.Main getInfo(String authorizationHeader) {
		String accessToken = authenticationProvider.extractToken(authorizationHeader);
		String userId = authenticationProvider.extractUserId(accessToken);
		List<String> roles = roleService.getMemberRoles(userId);

		return AuthInfo.Main.builder()
			.userId(userId)
			.roles(roles)
			.build();
	}

    public void deleteRefreshTokenByAccessToken(String authorizationHeader) {
        String at = authenticationProvider.extractToken(authorizationHeader);
        if (at == null || at.isBlank()) return;

        String userId = authenticationProvider.validateToken(at)
                ? authenticationProvider.extractUserId(at)
                : authenticationProvider.extractUserIdAllowExpired(at); // 서명 OK + 만료 시 sub

        if (userId == null || userId.isBlank()) return;

        authenticationProvider.revokeAllForUser(userId); // Redis RT 삭제
    }

	public ResponseCookie removeCookie() {
        return ResponseCookie.from("refresh-token", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .maxAge(0)
                .build();
	}

}
