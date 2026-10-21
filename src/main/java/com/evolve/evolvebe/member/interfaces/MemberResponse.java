package com.evolve.evolvebe.member.interfaces;

import java.time.ZonedDateTime;
import java.util.List;

import com.evolve.evolvebe.member.domain.MemberInfo;

import lombok.*;
import org.springframework.data.domain.Page;

public class MemberResponse {

	// 멤버 목록
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class MemberListResponse {
		private List<MemberInfo.Summary> memberInfos;

		public static MemberListResponse from(List<MemberInfo.Summary> members) {
			return MemberListResponse.builder()
					.memberInfos(members)
					.build();
		}
	}

	// 페이징 멤버 목록
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberPageResponse {
        private Page<MemberInfo.Summary> memberInfos;
    }

	// 단일 멤버 정보 응답
	@Getter
	@Builder
	public static class MemberDetailResponse {
		private String id;
		private String koName;
		private String enName;
		private String email;
		private String phoneNumber;
		private Boolean isActive;
		private Boolean mustChangePassword;
		private ZonedDateTime createdAt;
		private ZonedDateTime updatedAt;
	}

	// 멤버 등록 결과 응답 (간단한 확인용)
	@Getter
	@Builder
	@ToString
	public static class MemberCreateResponse {
		private String id;
		private String message;
	}

	@Getter
	@Builder
	@ToString
	public static class MemberUpdateResponse {
		private String id;
		private String koName;
		private String enName;
		private String email;
		private String phoneNumber;
		private Boolean isActive;
		private ZonedDateTime createdAt;
		private ZonedDateTime updatedAt;
	}

}
