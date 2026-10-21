package com.evolve.evolvebe.menu.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.menu.domain.MenuCommand;
import com.evolve.evolvebe.menu.domain.MenuInfo;
import com.evolve.evolvebe.menu.domain.MenuService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuFacade {

	private final MenuService menuService;

	@Transactional(readOnly = true)
	public MenuInfo.Main getMenu(Integer id) {
		return menuService.getMenu(id);
	}

	@Transactional(readOnly = true)
	public List<MenuInfo.Main> getMenus(Boolean isActive) {
		return menuService.getMenus(isActive);
	}

	@Transactional
	public MenuInfo.Main createMenu(MenuCommand.Create command) {
		return menuService.createMenu(command);
	}

	@Transactional
	public void updateMenu(Integer id, MenuCommand.Update command) {
		menuService.updateMenu(id, command);
	}

	@Transactional
	public void updateMenuPositions(MenuCommand.UpdatePosition command) {
		menuService.updateMenuPositions(command);
	}

	@Transactional
	public void deleteMenu(Integer id) {
		menuService.deleteMenu(id);
	}

}
