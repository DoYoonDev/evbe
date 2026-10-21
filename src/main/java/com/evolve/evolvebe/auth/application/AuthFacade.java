package com.evolve.evolvebe.auth.application;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.auth.domain.AuthCommand;
import com.evolve.evolvebe.auth.domain.AuthInfo;
import com.evolve.evolvebe.auth.domain.AuthService;
import com.evolve.evolvebe.auth.domain.Token;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthFacade {

	private final AuthService authService;

	@Transactional
	public Token.MemberToken generateLoginToken(AuthCommand.Login command) {
		authService.authenticateMember(command);
		Token.MemberToken token = authService.generateLoginToken(command.getUserId());
		authService.saveRefreshToken(command.getUserId(), token.getRefreshToken());
		return token;
	}

	@Transactional
	public ResponseCookie generateCookie(String refreshToken) {
		return authService.generateCookie(refreshToken);
	}

	@Transactional
	public Token.MemberToken refreshAccessToken(String refreshToken) {
		return authService.refreshAccessToken(refreshToken);
	}

	@Transactional(readOnly = true)
	public AuthInfo.Main getInfo(String authorizationHeader) {
		return authService.getInfo(authorizationHeader);
	}

    @Transactional
    public ResponseCookie logout(String authorizationHeader) {
        // 1) AccessToken 기반으로 Redis의 RT 삭제 (없는/만료된 경우 내부에서 무시)
        authService.deleteRefreshTokenByAccessToken(authorizationHeader);
        // 2) RT 쿠키 즉시 만료 쿠키 반환
        return authService.removeCookie();
    }
}
