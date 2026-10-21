package com.evolve.evolvebe.auth.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class AuthInfo {
	@Getter
	@Builder
	@ToString
	public static class Main {
		private String userId;
		private List<String> roles;
	}
}
