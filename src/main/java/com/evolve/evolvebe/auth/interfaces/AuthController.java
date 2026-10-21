package com.evolve.evolvebe.auth.interfaces;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evolve.evolvebe.auth.application.AuthFacade;
import com.evolve.evolvebe.auth.domain.AuthCommand;
import com.evolve.evolvebe.auth.domain.AuthInfo;
import com.evolve.evolvebe.auth.domain.Token;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthFacade authFacade;
	private final AuthDtoMapper authDtoMapper;

	@GetMapping("/info")
	public ResponseEntity<AuthResponse.InfoResponse> getInfo(
			@RequestHeader(value = "Authorization", required = false) final String authorizationHeader
	) {
		AuthInfo.Main authInfo = authFacade.getInfo(authorizationHeader);
		AuthResponse.InfoResponse response = authDtoMapper.from(authInfo);
		return ResponseEntity.ok(response);
	}

    @PostMapping("/login")
    public ResponseEntity<AuthResponse.TokenResponse> login(
            @RequestBody AuthRequest.Login request
    ) {
        AuthCommand.Login command = authDtoMapper.from(request);
        Token.MemberToken memberToken = authFacade.generateLoginToken(command);
        ResponseCookie cookie = authFacade.generateCookie(memberToken.getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())              // 쿠키 세팅
                .header(HttpHeaders.CACHE_CONTROL, "no-store")      // 캐시 방지
                .header(HttpHeaders.PRAGMA, "no-cache")             // 레거시 캐시 방지
                .body(authDtoMapper.from(memberToken));
    }

	@PostMapping("/refresh-token")
	public ResponseEntity<AuthResponse.TokenResponse> refreshToken(
			@CookieValue(value = "refresh-token", required = false) final String refreshCookie,
			@RequestHeader(value = "Refresh-Token", required = false) final String refreshHeader
	) {
		String refreshToken = (refreshCookie != null) ? refreshCookie : refreshHeader;
		Token.MemberToken memberToken = authFacade.refreshAccessToken(refreshToken);
		AuthResponse.TokenResponse response = authDtoMapper.from(memberToken);
		return ResponseEntity.ok(response);
	}

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        ResponseCookie cookie = authFacade.logout(authorization);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

}

