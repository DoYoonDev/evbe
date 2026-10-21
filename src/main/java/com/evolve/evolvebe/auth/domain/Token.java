package com.evolve.evolvebe.auth.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class Token {

	@Getter
	@Builder
	@ToString
	public static class MemberToken {
		private final String refreshToken;
		private final String accessToken;
	}

	@Getter
	@Builder
	@ToString
	public static class RefreshToken {
		private final String token;
		private final String userId;
	}

}
