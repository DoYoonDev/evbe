package com.evolve.evolvebe.menu.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.evolve.evolvebe.menu.domain.Menu;
import com.evolve.evolvebe.menu.domain.MenuRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

	private final MenuJpaRepository menuJpaRepository;

	@Override
	public List<Menu> getMenus(Boolean isActive) {
		if (isActive == null) {
			return menuJpaRepository.findByIsDeleteOrderBySortOrder(Boolean.FALSE);
		}
		return menuJpaRepository.findByIsDeleteAndIsActiveOrderBySortOrder(Boolean.FALSE, Boolean.TRUE);
	}

	@Override
	public List<Menu> getMenus(Integer parentMenuId) {
		return menuJpaRepository.findByParentMenuId(parentMenuId);
	}

	@Override
	public Optional<Menu> getMenu(Integer id) {
		return menuJpaRepository.findById(id);
	}

	@Override
	public Menu saveMenu(Menu menu) {
		return menuJpaRepository.save(menu);
	}
}
