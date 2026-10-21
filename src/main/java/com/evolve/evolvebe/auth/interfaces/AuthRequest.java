package com.evolve.evolvebe.auth.interfaces;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class AuthRequest {
	@Getter
	@Setter
	@ToString
	public static class Login {
		private String userId;
		private String password;
	}
}
