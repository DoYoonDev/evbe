package com.evolve.evolvebe.role.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

	List<Role> getRoles();

	Optional<Role> getRole(String roleId);

	List<RoleMenu> getRoleMenus(String roleId);

	List<RoleMenu> getRoleMenus(List<String> roleId);

	Optional<RoleMember> getRoleMember(String roleId, String memberId);

	List<RoleMember> getRoleMembers(String roleId);

    Page<RoleMember> getRoleMembersPaged(String roleId, String memberId, Pageable pageable);

	List<RoleMember> getMemberRoles(String memberId);

	List<RoleApi> getRoleApis(String roleId);

	void createRole(Role role);

	void deleteRole(Role role);

	void addMemberToRole(RoleMember roleMember);

	void deleteMemberFromRole(RoleMember roleMember);

	void deleteMenuRoles(Role role);

	void addMenuRoles(List<RoleMenu> roleMenus);

	void deleteByMemberId(String memberId);

    Optional<String> findTopRoleIdByIdsOrderByLevelDesc(List<String> roleIds);
}
