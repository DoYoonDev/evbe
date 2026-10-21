package com.evolve.evolvebe.menu;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.menu.application.MenuFacade;
import com.evolve.evolvebe.menu.domain.Menu;
import com.evolve.evolvebe.menu.domain.MenuCommand;
import com.evolve.evolvebe.menu.domain.MenuInfo;
import com.evolve.evolvebe.menu.domain.MenuRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MenuIntegrationTest {

	@Autowired
	private MenuFacade menuFacade;

	@Autowired
	private MenuRepository menuRepository;

	@Nested
	@DisplayName("메뉴 ID로 조회")
	class GetMenu {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("메뉴 ID로 조회")
			void getMenu() {
				// given
				Integer id = 1;

				// when
				MenuInfo.Main result = menuFacade.getMenu(id);

				// then
				assertEquals(id, result.getId());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailureCase {

			@Test
			@DisplayName("메뉴 ID로 조회")
			void getMenu() {
				// given
				Integer id = 100;

				// when & then
				assertThrows(CustomException.class, () -> menuFacade.getMenu(id));
			}
		}
	}

	@Test
	@DisplayName("메뉴 목록 조회")
	void getMenus() {
		// given
		Boolean isActive = true;

		// when
		List<MenuInfo.Main> result = menuFacade.getMenus(isActive);

		assertNotNull(result);
	}

	@Test
	@DisplayName("메뉴 생성")
	void createMenu() {
		// given
		MenuCommand.Create menu = MenuCommand.Create.builder()
			.parentMenuId(1)
			.name("test")
			.level(2)
			.isActive(true)
			.build();

		// when
		MenuInfo.Main result = menuFacade.createMenu(menu);

		// then
		assertEquals(menu.getName(), result.getName());
	}

	@Test
	@DisplayName("메뉴 수정")
	void updateMenu() {
		// given
		Integer id = 1;
		MenuCommand.Update menu = MenuCommand.Update.builder()
			.name("test")
			.isActive(true)
			.build();

		// when
		menuFacade.updateMenu(id, menu);

		// then
		Optional<Menu> result = menuRepository.getMenu(id);
		assertNotNull(result);
	}

	@Test
	@DisplayName("메뉴 순서 수정")
	void updateMenuPosition() {
		// given
		MenuCommand.UpdatePosition menu = MenuCommand.UpdatePosition.builder()
			.level1(List.of(5, 1, 2, 3, 4))
			.level2(Map.of(
				"1", List.of(6, 7, 8),
				"2", List.of(9),
				"3", List.of(12),
				"4", List.of(13),
				"5", List.of(14, 15, 17)
			))
			.level3(Map.of(
				"9", List.of(10, 11),
				"17", List.of(20, 18, 19)
			))
			.build();

		// when
		menuFacade.updateMenuPositions(menu);

		// then
		Optional<Menu> result = menuRepository.getMenu(5);
		assertTrue(result.isPresent());
		assertEquals(1, result.get().getSortOrder());
	}

	@Test
	@DisplayName("메뉴 삭제")
	void deleteMenu() {
		// given
		Integer id = 1;

		// when
		menuFacade.deleteMenu(id);

		// then
		Optional<Menu> result = menuRepository.getMenu(id);
		assertTrue(result.isPresent());
	}

}
