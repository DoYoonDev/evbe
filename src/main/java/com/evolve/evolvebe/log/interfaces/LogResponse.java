package com.evolve.evolvebe.log.interfaces;

import com.evolve.evolvebe.log.domain.LogInfo;

import lombok.*;

import org.springframework.data.domain.Page;

public class LogResponse {

	@Getter
	@Builder
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class LogAccessPage {
		private Page<LogInfo.LogAccess> logAccessInfos;
//		private int page;
//		private int size;
//		private long totalElements;
//		private int totalPages;

//		public static LogResponse.LogAccessPage from(Page<LogInfo.LogAccess> pageData) {
//			return LogResponse.LogAccessPage.builder()
//					.logAccessInfos(pageData.getContent())
//					.page(pageData.getNumber())
//					.size(pageData.getSize())
//					.totalElements(pageData.getTotalElements())
//					.totalPages(pageData.getTotalPages())
//					.build();
//		}
	}

	@Getter
	@Builder
	@ToString
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class LogLoginPage {
		private Page<LogInfo.LogLogin> logLoginInfos;
//		private int page;
//		private int size;
//		private long totalElements;
//		private int totalPages;
//
//		public static LogResponse.LogLoginPage from(Page<LogInfo.LogLogin> pageData) {
//			return LogResponse.LogLoginPage.builder()
//					.logLoginInfos(pageData.getContent())
//					.page(pageData.getNumber())
//					.size(pageData.getSize())
//					.totalElements(pageData.getTotalElements())
//					.totalPages(pageData.getTotalPages())
//					.build();
//		}
	}
}
