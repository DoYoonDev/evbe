package com.evolve.evolvebe.auth.infra;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.evolve.evolvebe.auth.domain.AuthCommand;
import com.evolve.evolvebe.auth.domain.AuthenticationProvider;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider {

	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final TokenRedisRepository tokenRedisRepository;
	//private final BearerAuthorizationExtractor bearerAuthorizationExtractor;

	@Override
	public void authenticate(AuthCommand.Login command) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(command.getUserId(), command.getPassword())
		);
	}

	@Override
	public String generateRefreshToken(String userId) {
		return jwtProvider.generateRefreshToken(userId);
	}

	@Override
	public String generateAccessToken(String userId) {
		return jwtProvider.generateAccessToken(userId);
	}

	@Override
	public void saveRefreshToken(String userId, String refreshToken) {
		tokenRedisRepository.saveRefreshToken(userId, refreshToken);
	}

	@Override
	public String regenerateToken(String refreshToken) {
		if (refreshToken == null || refreshToken.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		try {
			jwtProvider.ensureValidRefreshToken(refreshToken);
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
		} catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 2) subject 추출 (만료가 아니라서 getSubject 가능)
		final String userId = jwtProvider.getSubject(refreshToken);
		if (userId == null || userId.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		// 3) Redis에 저장된 refresh 토큰과 일치하는지 확인
		final String stored = tokenRedisRepository.findRefreshToken(userId).orElse(null);
		if (stored == null || !stored.equals(refreshToken)) {
			// 토큰 로테이션/탈취 방지: 저장값과 다르면 거부
			throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		return jwtProvider.generateAccessToken(userId);
	}

	@Override
	public String extractToken(String authorizationHeader) {
		//return bearerAuthorizationExtractor.extractAccessToken(authorizationHeader);
		return jwtProvider.extractAccessToken(authorizationHeader);
	}

	@Override
	public boolean validateToken(String token) {
		return jwtProvider.validateToken(token);
	}

	@Override
	public String extractUserId(String token) {
		return jwtProvider.getSubject(token);
	}

	@Override
	public void revokeAllForUser(String userId) {
		tokenRedisRepository.deleteRefreshToken(userId);
	}

    @Override
    public String extractUserIdAllowExpired(String token) {
        if (token == null || token.isBlank()) return null;
        return jwtProvider.getSubjectAllowExpired(token);
    }
}
