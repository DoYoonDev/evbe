package com.evolve.evolvebe.role.domain;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface RoleInfoMapper {

	@IterableMapping(qualifiedByName = "fromRole")
	List<RoleInfo.Main> from(List<Role> roles);

	@IterableMapping(qualifiedByName = "fromRoleMenu")
	List<RoleInfo.Menu> mapToMenuInfoList(List<RoleMenu> roleMenus);

	@IterableMapping(qualifiedByName = "fromRoleMember")
	List<RoleInfo.Member> mapToMemberInfoList(List<RoleMember> roleMembers);

	@IterableMapping(qualifiedByName = "fromRoleApi")
	List<RoleInfo.Api> mapToApiInfoList(List<RoleApi> roleApis);

	@Named("fromRole")
	RoleInfo.Main from(Role role);

	@Named("fromRoleMenu")
	RoleInfo.Menu from(RoleMenu roleMenu);

	@Named("fromRoleMember")
	@Mappings({
		@Mapping(source = "member.id", target = "memberId"),
		@Mapping(source = "member.koName", target = "koName"),
		@Mapping(source = "member.enName", target = "enName")
	})
	RoleInfo.Member from(RoleMember roleMember);

	@Named("fromRoleApi")
	RoleInfo.Api from(RoleApi roleApi);

    default Page<RoleInfo.Member> mapToMemberInfoPage(Page<RoleMember> roleMembers) {
        if (roleMembers == null) return Page.empty();
        return roleMembers.map(this::from);
    }
}
