package com.evolve.evolvebe.log.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

public class LogInfo {

	@Getter
	@Builder
	@ToString
	@AllArgsConstructor
	public static class LogAccess {
		private final String id;
		private final String memberId;
		private final String requestUrl;
		private final String requestMethod;
		private final String requestPayload;
		private final String requestIp;
		private final Integer responseStatus;
		private final String responseBody;
		private final ZonedDateTime createdAt;
	}

	@Getter
	@Builder
	@ToString
	@AllArgsConstructor
	public static class LogLogin {
		private final String id;
		private final String memberId;
		private final String accessIp;
		private final boolean success;
		private final ZonedDateTime createdAt;
	}
}
