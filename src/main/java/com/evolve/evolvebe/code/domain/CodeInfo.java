package com.evolve.evolvebe.code.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class CodeInfo {

	@Getter
	@Builder
	@ToString
	public static class Main {
		private final String id;
		private final CodeGroup codeGroup;
		private final String messageId;
		private final String name;
		private final String description;
		private final Integer sortOrder;
		private final Boolean isActive;
		private final String createMemberId;
		private final String updateMemberId;
	}

	@Getter
	@Builder
	@ToString
	public static class CodeGroupInfo {
		private final String id;
		private final String name;
		private final String description;
		private final Boolean isActive;
		private final String createMemberId;
		private final String updateMemberId;
	}

}
