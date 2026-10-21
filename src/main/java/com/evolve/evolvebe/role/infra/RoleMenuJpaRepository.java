package com.evolve.evolvebe.role.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.evolve.evolvebe.role.domain.RoleMenu;

import jakarta.transaction.Transactional;

public interface RoleMenuJpaRepository extends JpaRepository<RoleMenu, Long> {
	List<RoleMenu> findAllByRoleIdAndMenuIsActiveTrue(String roleId);

	List<RoleMenu> findAllByRoleIdInAndMenuIsActiveTrue(List<String> roleId);

	@Modifying
	@Transactional
	@Query("delete from RoleMenu rm where rm.role.id = :roleId")
	void deleteByRoleId(@Param("roleId") String roleId);
}
