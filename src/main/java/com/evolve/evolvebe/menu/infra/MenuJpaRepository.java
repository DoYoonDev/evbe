package com.evolve.evolvebe.menu.infra;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.menu.domain.Menu;

public interface MenuJpaRepository extends JpaRepository<Menu, Integer> {

	List<Menu> findByIsDeleteOrderBySortOrder(Boolean isDelete);

	List<Menu> findByIsDeleteAndIsActiveOrderBySortOrder(Boolean isDelete, Boolean isActive);

	List<Menu> findByParentMenuId(Integer parentMenuId);
}
