package com.evolve.evolvebe.menu.domain;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

	Optional<Menu> getMenu(Integer id);

	List<Menu> getMenus(Boolean isActive);

	List<Menu> getMenus(Integer parentMenuId);

	Menu saveMenu(Menu menu);
}
