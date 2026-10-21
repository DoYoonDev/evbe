package com.evolve.evolvebe.log.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class LogCommand {

	@Getter
	@Setter
	@Builder
	public static class GetLogAccessList {
		private String memberId;
//		private String requestUrl;
//		private String requestMethod;
//		private String requestIp;
		private String accessedAtStart;
		private String accessedAtEnd;
	}

	@Getter
	@Setter
	@Builder
	public static class CreateLogAccess {
		private String memberId;
		private String requestUrl;
		private String requestMethod;
		private String requestPayload;
		private String requestIp;
		private Integer responseStatus;
		private String responseBody;
		private String userAgent;

		public LogAccess toEntity() {
			return LogAccess.builder()
				.memberId(memberId)
				.requestUrl(requestUrl)
				.requestMethod(requestMethod)
				.requestPayload(requestPayload)
				.requestIp(requestIp)
				.responseStatus(responseStatus)
				.responseBody(responseBody)
				.userAgent(userAgent)
				.build();
		}
	}

	@Getter
	@Setter
	@Builder
	public static class GetLogLoginList {
		private String memberId;
		private String accessedAtStart;
		private String accessedAtEnd;
		private boolean success;
	}

	@Getter
	@Setter
	@Builder
	public static class CreateLogLogin {
		private String memberId;
		private String accessIp;
		private String userAgent;
		private boolean success;

		public LogLogin toEntity() {
			return LogLogin.builder()
				.memberId(memberId)
				.accessIp(accessIp)
				.userAgent(userAgent)
				.success(success)
				.build();
		}
	}
}
