package com.evolve.evolvebe.auth.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthCommand {
	@Getter
	@Setter
	@ToString
	public static class Login {
		private String userId;
		private String password;
	}
}
