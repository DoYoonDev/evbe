package com.evolve.evolvebe.role.application;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.menu.application.MenuFacade;
import com.evolve.evolvebe.menu.domain.MenuInfo;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.domain.RoleInfo;
import com.evolve.evolvebe.role.domain.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleFacade {

	private final RoleService roleService;
	private final MenuFacade menuFacade;

	@Transactional(readOnly = true)
	public List<RoleInfo.Main> getRoles() {
		return roleService.getRoles();
	}

	@Transactional(readOnly = true)
	public RoleInfo.Main getRole(String roleId) {
		return roleService.getRole(roleId);
	}

	@Transactional(readOnly = true)
	public List<RoleInfo.Menu> getMenusByRoleId(String roleId) {
		List<MenuInfo.Main> menusInfos = menuFacade.getMenus(true);
		List<Integer> roleMenuIds = roleService.getRoleMenuIds(roleId);

		return menusInfos.stream()
			.map(menu -> RoleInfo.Menu.builder()
				.menu(menu)
				.isChecked(roleMenuIds.contains(menu.getId()))
				.build())
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<RoleInfo.Menu> getMenusByRoleIds(List<String> roleIds) {
		return roleService.getMenusByRoles(roleIds);
	}

	@Transactional(readOnly = true)
	public List<RoleInfo.Member> getMembersByRoleId(String roleId) {
		return roleService.getRoleMembers(roleId);
	}

    @Transactional(readOnly = true)
    public Page<RoleInfo.Member> getMembersByRoleIdPaged(String roleId, String memberId, Pageable pageable) {
        return roleService.getRoleMembersPaged(roleId, memberId, pageable);
    }

    @Transactional(readOnly = true)
	public List<String> getMemberRoles(String memberId) {
		return roleService.getMemberRoles(memberId);
	}

	@Transactional(readOnly = true)
	public List<String> getMenusUrlByRoles(List<String> roleIds) {
		return roleService.getMenusUrlByRoles(roleIds);
	}

	@Transactional(readOnly = true)
	public List<RoleInfo.Api> getApisByRoleId(String roleId) {
		return roleService.getApisByRoleId(roleId);
	}

	@Transactional
	public void createRole(RoleCommand.CreateRole createRole) {
		roleService.createRole(createRole);
	}

	@Transactional
	public void updateRole(RoleCommand.UpdateRole updateRole, String roleId) {
		roleService.updateRole(updateRole, roleId);
	}

	@Transactional
	public void deleteRole(String roleId) {
		roleService.deleteRole(roleId);
	}

	@Transactional
	public void addMemberToRole(RoleCommand.AddMember command) {
		roleService.addMemberToRole(command);
	}

	@Transactional
	public void deleteMemberFromRole(RoleCommand.DeleteMember command) {
		roleService.deleteMemberFromRole(command);
	}

	@Transactional
	public void updateRoleMenus(String roleId, Integer[] menuIds) {
		roleService.updateRoleMenus(roleId, menuIds);
	}
}
