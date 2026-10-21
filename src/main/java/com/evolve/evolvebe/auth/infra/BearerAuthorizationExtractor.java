package com.evolve.evolvebe.auth.infra;

import org.springframework.stereotype.Component;

@Component
public class BearerAuthorizationExtractor {

	private static final String BEARER_TYPE = "Bearer ";

	public String extractAccessToken(String header) {
		if (header != null && header.startsWith(BEARER_TYPE)) {
			return header.substring(BEARER_TYPE.length()).trim();
		}
		return "";
	}
}
