package com.evolve.evolvebe.role.interfaces;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.domain.RoleInfo;
import org.springframework.data.domain.Page;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface RoleDtoMapper {

	default RoleResponse.RoleListResponse mapToRoleList(List<RoleInfo.Main> roles) {
		return RoleResponse.RoleListResponse.builder()
			.roleInfos(roles)
			.build();
	}

	default RoleResponse.MenuListResponse mapToMenuList(List<RoleInfo.Menu> menus) {
		return RoleResponse.MenuListResponse.builder()
			.roleMenuInfos(menus)
			.build();
	}

	default RoleResponse.MemberListResponse mapToMemberList(List<RoleInfo.Member> members) {
		return RoleResponse.MemberListResponse.builder()
			.roleMemberInfos(members)
			.build();
	}

    default RoleResponse.MemberRoles mapToMemberRole(String memberId, List<String> roles) {
        return RoleResponse.MemberRoles.builder()
                .memberId(memberId)
                .roles(roles == null ? List.of() : List.copyOf(roles))
                .build();
    }

    default RoleResponse.MemberPageResponse mapToMemberPage(Page<RoleInfo.Member> page) {
        return RoleResponse.MemberPageResponse.builder()
                .roleMemberInfos(page == null ? Page.empty() : page)
                .build();
    }

    default RoleResponse.ApiListResponse mapToApiList(List<RoleInfo.Api> apis) {
		return RoleResponse.ApiListResponse.builder()
			.apiInfos(apis)
			.build();
	}

	@Mapping(source = "role", target = "roleInfo")
	RoleResponse.RoleInfoResponse from(RoleInfo.Main role);

	RoleCommand.CreateRole from(RoleRequest.CreateRole createRole);

	RoleCommand.UpdateRole from(RoleRequest.UpdateRole updateRole);

	RoleCommand.AddMember mapToAddCommand(String id, String memberId);

	RoleCommand.DeleteMember mapToDeleteCommand(String id, String memberId);

}
