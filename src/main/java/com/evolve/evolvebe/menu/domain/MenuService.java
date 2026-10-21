package com.evolve.evolvebe.menu.domain;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final MenuInfoMapper menuInfoMapper;

	public MenuInfo.Main getMenu(Integer id) {
		Menu menu = menuRepository.getMenu(id)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));
		return menuInfoMapper.from(menu);
	}

	public List<MenuInfo.Main> getMenus(Boolean isActive) {
		List<Menu> menus = menuRepository.getMenus(isActive);
		return menuInfoMapper.from(menus);
	}

	public MenuInfo.Main createMenu(MenuCommand.Create command) {
		validateMenuHierarchy(command.getParentMenuId(), command.getLevel());
		Menu menu = menuRepository.saveMenu(command.toEntity());
		return menuInfoMapper.from(menu);
	}

	public void updateMenu(Integer id, MenuCommand.Update command) {
		Menu menu = menuRepository.getMenu(id)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));

		menu.update(
			command.getMessageId(),
			command.getUrl(),
			command.getName(),
			command.getIsActive(),
			command.getDescription()
		);

	}

	public void updateMenuPositions(MenuCommand.UpdatePosition command) {

		// level 1
		List<Integer> level1 = command.getLevel1();

		for (int i = 0; i < level1.size(); i++) {
			Menu menu = menuRepository.getMenu(level1.get(i))
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));

			menu.update(null, i + 1, 1);
		}

		// level 2
		Map<String, List<Integer>> level2 = command.getLevel2();
		for (Map.Entry<String, List<Integer>> entry : level2.entrySet()) {
			String parentMenuId = entry.getKey();
			List<Integer> level2Ids = entry.getValue();
			for (int i = 0; i < level2Ids.size(); i++) {
				Menu menu = menuRepository.getMenu(level2Ids.get(i))
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));

				menu.update(Integer.parseInt(parentMenuId), i + 1, 2);
			}
		}

		// level 3
		Map<String, List<Integer>> level3 = command.getLevel3();
		for (Map.Entry<String, List<Integer>> entry : level3.entrySet()) {
			String parentMenuId = entry.getKey();
			List<Integer> level3Ids = entry.getValue();
			for (int i = 0; i < level3Ids.size(); i++) {
				Menu menu = menuRepository.getMenu(level3Ids.get(i))
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));

				menu.update(Integer.parseInt(parentMenuId), i + 1, 3);
			}
		}

	}

	public void deleteMenu(Integer id) {
		Menu menu = menuRepository.getMenu(id)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MENU));
		menu.update(Boolean.TRUE);

		fetchSubMenus(id);
	}

	/**
	 * 메뉴 계층 구조 검증
	 * 1. 부모 메뉴 ID가 null인 경우, 레벨은 1
	 * 2. 부모 메뉴 ID가 null이 아닌 경우:
	 *    - 부모 메뉴가 반드시 존재
	 *    - 부모 메뉴 레벨 < 자식 메뉴 레벨
	 *
	 * @param parentMenuId 부모 메뉴 ID
	 * @param level        현재 메뉴 레벨
	 * @throws CustomException 메뉴 계층 구조가 잘못된 경우
	 */
	private void validateMenuHierarchy(
		Integer parentMenuId,
		Integer level
	) {
		// 상위 메뉴 == null &&n 레벨 != 1
		if (parentMenuId == null && level != 1) {
			throw new CustomException(ErrorCode.INVALID_MENU_HIERARCHY);
		}
		// 상위 메뉴 != null
		if (parentMenuId != null) {
			Menu parentMenu = menuRepository.getMenu(parentMenuId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PARENT_MENU));

			// 상위 메뉴 레벨 > 자식 메뉴 레벨
			if (parentMenu.getLevel() >= level) {
				throw new CustomException(ErrorCode.INVALID_PARENT_MENU_LEVEL);
			}
		}
	}

	/**
	 * 주어진 메뉴 ID를 기준으로 하위 메뉴를 재귀적으로 조회하여 리스트에 추가하는 메서드
	 *
	 * @param id    조회할 부모 메뉴의 ID
	 */
	private void fetchSubMenus(Integer id) {
		List<Menu> subMenus = menuRepository.getMenus(id);
		for (Menu menu : subMenus) {
			menu.update(Boolean.TRUE);
			fetchSubMenus(menu.getId());
		}
	}

}
