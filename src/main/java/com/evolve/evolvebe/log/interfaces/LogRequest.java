package com.evolve.evolvebe.log.interfaces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class LogRequest {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class GetLogAccessList {
		private String memberId = "";
//		private String requestUrl = "";
//		private String requestMethod = "";
//		private String requestIp = "";
		private String accessedAtStart;
		private String accessedAtEnd;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class GetLogLoginList {
		private String memberId = "";
		private String accessedAtStart;
		private String accessedAtEnd;
		private boolean success;
	}

}
