package com.evolve.evolvebe.role.domain;

import java.util.*;

import com.evolve.evolvebe.role.infra.RoleMemberJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.member.domain.Member;
import com.evolve.evolvebe.member.domain.MemberRepository;
import com.evolve.evolvebe.menu.domain.Menu;
import com.evolve.evolvebe.menu.domain.MenuRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	private final RoleInfoMapper roleInfoMapper;
	private final MemberRepository memberRepository;
	private final MenuRepository menuRepository;
    private final RoleMemberJpaRepository roleMemberRepository;

	public List<RoleInfo.Main> getRoles() {
		List<Role> roles = roleRepository.getRoles();
		return roleInfoMapper.from(roles);
	}

	public RoleInfo.Main getRole(String roleId) {
		Role role = roleRepository.getRole(roleId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROLE));
		return roleInfoMapper.from(role);
	}

	public List<RoleInfo.Menu> getRoleMenus(String roleId) {
		List<RoleMenu> roleMenus = roleRepository.getRoleMenus(roleId);

		return roleInfoMapper.mapToMenuInfoList(roleMenus);
	}

	public List<Integer> getRoleMenuIds(String roleId) {
		List<RoleMenu> roleMenus = roleRepository.getRoleMenus(roleId);

		return roleMenus.stream()
			.map(roleMenu -> roleMenu.getMenu().getId())
			.toList();
	}

	public List<RoleInfo.Member> getRoleMembers(String roleId) {
		List<RoleMember> roleMembers = roleRepository.getRoleMembers(roleId);

		return roleInfoMapper.mapToMemberInfoList(roleMembers);
	}

    public Page<RoleInfo.Member> getRoleMembersPaged(String roleId, String memberId, Pageable pageable) {
        Page<RoleMember> page = roleRepository.getRoleMembersPaged(roleId, memberId, pageable);
        return roleInfoMapper.mapToMemberInfoPage(page);
    }

    public List<String> getMemberRoles(String memberId) {
		List<RoleMember> roleMembers = roleRepository.getMemberRoles(memberId);

		return roleMembers.stream()
			.map(roleMember -> roleMember.getRole().getId())
			.toList();
	}

    private Optional<String> getTopRoleIdByRoles(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Optional.empty();
        }
        return roleRepository.findTopRoleIdByIdsOrderByLevelDesc(roleIds);
    }

    public List<String> getMenusUrlByRoles(List<String> roleIds) {
        Optional<String> topRoleIdOpt = getTopRoleIdByRoles(roleIds);
        if (topRoleIdOpt.isEmpty()) {
            return Collections.emptyList();
        }

        List<RoleMenu> roleMenus = roleRepository.getRoleMenus(topRoleIdOpt.get());
        return roleMenus.stream()
                .map(rm -> rm.getMenu().getUrl())
                .toList();
    }

    public List<RoleInfo.Menu> getMenusByRoles(List<String> roleIds) {
        Optional<String> topRoleIdOpt = getTopRoleIdByRoles(roleIds);
        if (topRoleIdOpt.isEmpty()) {
            return Collections.emptyList();
        }

        List<RoleMenu> roleMenus = roleRepository.getRoleMenus(topRoleIdOpt.get());
        return roleInfoMapper.mapToMenuInfoList(roleMenus);
    }

	public List<RoleInfo.Api> getApisByRoleId(String roleId) {
		List<RoleApi> roleApis = roleRepository.getRoleApis(roleId);

		return roleInfoMapper.mapToApiInfoList(roleApis);
	}

	public void createRole(RoleCommand.CreateRole createRole) {
        roleRepository.createRole(createRole.toEntity());
	}

	public void updateRole(RoleCommand.UpdateRole updateRole, String roleId) {
		Role role = roleRepository.getRole(roleId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROLE));

        Integer newLevel = Optional.ofNullable(updateRole.getLevel()).orElse(0);
		role.update(
			updateRole.getName(),
			updateRole.getDescription(),
			updateRole.getIsActive(),
            newLevel
		);
	}

	public void deleteRole(String roleId) {
		Role role = roleRepository.getRole(roleId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROLE));

		roleRepository.deleteRole(role);
	}

	public void addMemberToRole(RoleCommand.AddMember command) {
		Role role = roleRepository.getRole(command.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROLE));
		Member member = memberRepository.getMember(command.getMemberId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        if (roleMemberRepository.existsByRoleIdAndMemberId(role.getId(), member.getId())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_MEMBER);
        }

        RoleMember roleMember = RoleMember.builder()
                .role(role)
                .member(member)
                .build();

        roleRepository.addMemberToRole(roleMember);
	}

	public void deleteMemberFromRole(RoleCommand.DeleteMember command) {
		RoleMember roleMember = roleRepository.getRoleMember(command.getId(), command.getMemberId())
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROLE_MEMBER));

		roleRepository.deleteMemberFromRole(roleMember);
	}

	public void updateRoleMenus(String roleId, Integer[] menuIds) {
		Role role = roleRepository.getRole(roleId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_ROLE));

        // null/빈 배열 방어
        if (menuIds == null || menuIds.length == 0) {
            // 메뉴 전부 제거
            roleRepository.deleteMenuRoles(role);
            return;
        }

        List<RoleMenu> roleMenus = new ArrayList<>(menuIds.length);
		for (Integer menuId : menuIds) {
			Menu menu = menuRepository.getMenu(menuId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));
			RoleMenu roleMenu = RoleMenu.builder()
				.role(role)
				.menu(menu)
				.build();
			roleMenus.add(roleMenu);
		}

		roleRepository.deleteMenuRoles(role);
		roleRepository.addMenuRoles(roleMenus);
	}

	public void removeAllRolesOfMember(String memberId) {
        roleRepository.deleteByMemberId(memberId);
	}

}
