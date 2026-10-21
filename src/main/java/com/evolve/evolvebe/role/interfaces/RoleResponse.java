package com.evolve.evolvebe.role.interfaces;

import java.util.List;

import com.evolve.evolvebe.role.domain.RoleInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

public class RoleResponse {

	@Getter
	@Builder
	@ToString
	public static class RoleListResponse {
		private List<RoleInfo.Main> roleInfos;
	}

	@Getter
	@Builder
	@ToString
	public static class RoleInfoResponse {
		private RoleInfo.Main roleInfo;
	}

	@Getter
	@Builder
	@ToString
	public static class MenuListResponse {
		private List<RoleInfo.Menu> roleMenuInfos;
	}

	@Getter
	@Builder
	@ToString
	public static class MemberListResponse {
		private List<RoleInfo.Member> roleMemberInfos;
	}

	@Getter
	@Builder
	@ToString
	public static class ApiListResponse {
		private List<RoleInfo.Api> apiInfos;
	}

	@Getter
	@Builder
	@ToString
	public static class MemberRoles {
		private String memberId;
		private List<String> roles;
	}

    @Getter
    @Builder
    public static class MemberPageResponse {
        private Page<RoleInfo.Member> roleMemberInfos;
    }

}
