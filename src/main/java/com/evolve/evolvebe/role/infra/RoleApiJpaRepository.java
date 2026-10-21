package com.evolve.evolvebe.role.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.role.domain.RoleApi;

public interface RoleApiJpaRepository extends JpaRepository<RoleApi, Long> {
	List<RoleApi> findAllByRoleId(String roleId);
}
