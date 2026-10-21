package com.evolve.evolvebe.auth.interfaces;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class AuthResponse {
	@Getter
	@Builder
	@ToString
	public static class TokenResponse {
		private String accessToken;
	}

	@Getter
	@Builder
	@ToString
	public static class InfoResponse {
		private String userId;
		private List<String> roles;
	}
}
