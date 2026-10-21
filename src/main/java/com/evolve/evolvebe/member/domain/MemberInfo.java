package com.evolve.evolvebe.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

public class MemberInfo {

	@Getter
	@Builder
	@ToString(exclude = "password")
	@AllArgsConstructor
	public static class Main {
		private final String id;
		private final String koName;
		private final String enName;
		private final String email;
		private final String phoneNumber;
		private final String password;
		private final Boolean isActive;
		private final Boolean mustChangePassword;
		private final ZonedDateTime createdAt;
		private final ZonedDateTime updatedAt;
	}

	@Getter
	@Builder
	@ToString
	@AllArgsConstructor
	public static class Profile {             // 외부 상세
		private final String id;
		private final String koName;
		private final String enName;
		private final String email;
		private final String phoneNumber;
		private final Boolean isActive;
		private final Boolean mustChangePassword;
		private final ZonedDateTime createdAt;
		private final ZonedDateTime updatedAt;
	}

	@Getter
	@Builder
	@ToString
	@AllArgsConstructor
	public static class Summary {             // 외부 목록
		private final String id;
		private final String koName;
		private final String enName;
		private final String email;
		private final String phoneNumber;
		private final Boolean isActive;
		private final Boolean mustChangePassword;
		private final ZonedDateTime createdAt;
		private final ZonedDateTime updatedAt;
	}
}
