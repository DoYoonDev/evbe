package com.evolve.evolvebe.role.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.evolve.evolvebe.role.domain.Role;
import com.evolve.evolvebe.role.domain.RoleApi;
import com.evolve.evolvebe.role.domain.RoleMember;
import com.evolve.evolvebe.role.domain.RoleMenu;
import com.evolve.evolvebe.role.domain.RoleRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

	private final RoleJpaRepository roleJpaRepository;
	private final RoleMenuJpaRepository roleMenuJpaRepository;
	private final RoleMemberJpaRepository roleMemberJpaRepository;
	private final RoleApiJpaRepository roleApiJpaRepository;

	@Override
	public List<Role> getRoles() {
		return roleJpaRepository.findAll();
	}

	@Override
	public Optional<Role> getRole(String roleId) {
		return roleJpaRepository.findById(roleId);
	}

	@Override
	public Optional<RoleMember> getRoleMember(String roleId, String memberId) {
		return roleMemberJpaRepository.findByRoleIdAndMemberId(roleId, memberId);
	}

	@Override
	public List<RoleMember> getMemberRoles(String memberId) {
		return roleMemberJpaRepository.findAllByMemberId(memberId);
	}

	@Override
	public List<RoleMenu> getRoleMenus(List<String> roleId) {
		return roleMenuJpaRepository.findAllByRoleIdInAndMenuIsActiveTrue(roleId);
	}

	@Override
	public List<RoleMember> getRoleMembers(String roleId) {
		return roleMemberJpaRepository.findAllByRoleId(roleId);
	}

    @Override
    public Page<RoleMember> getRoleMembersPaged(String roleId, String memberId, Pageable pageable) {
        return roleMemberJpaRepository.findByRoleIdAndMemberIdContaining(roleId, memberId, pageable);
    }

	@Override
	public List<RoleMenu> getRoleMenus(String roleId) {
		return roleMenuJpaRepository.findAllByRoleIdAndMenuIsActiveTrue(roleId);
	}

	@Override
	public List<RoleApi> getRoleApis(String roleId) {
		return roleApiJpaRepository.findAllByRoleId(roleId);
	}

	@Override
	public void createRole(Role role) {
		roleJpaRepository.save(role);
	}

	@Override
	public void deleteRole(Role role) {
		roleJpaRepository.delete(role);
	}

	@Override
	public void addMemberToRole(RoleMember roleMember) {
		roleMemberJpaRepository.save(roleMember);
	}

	@Override
	public void deleteMemberFromRole(RoleMember roleMember) {
		roleMemberJpaRepository.delete(roleMember);
	}

	@Override
	public void addMenuRoles(List<RoleMenu> roleMenus) {
		roleMenuJpaRepository.saveAll(roleMenus);
	}

	@Override
	public void deleteMenuRoles(Role role) {
		roleMenuJpaRepository.deleteByRoleId(role.getId());
	}

	@Override
	public void deleteByMemberId(String memberId) {
		roleMemberJpaRepository.deleteAllByMemberId(memberId);
	}

    @Override
    public Optional<String> findTopRoleIdByIdsOrderByLevelDesc(List<String> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Optional.empty();
        List<String> topIds = roleJpaRepository
                .findTopIdsByIdsOrderByLevelDesc(roleIds);
        return topIds.isEmpty() ? Optional.empty() : Optional.of(topIds.get(0));
    }
}
