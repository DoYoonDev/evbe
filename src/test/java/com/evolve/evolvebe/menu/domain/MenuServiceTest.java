package com.evolve.evolvebe.menu.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private MenuInfoMapper menuInfoMapper;

	@InjectMocks
	private MenuService menuService;

	@Nested
	@DisplayName("메뉴 ID로 조회")
	class GetMenu {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("getMenu: 성공")
			void getMenu() {
				// given
				Integer id = 1;
				Menu menu = Menu.builder().build();
				when(menuRepository.getMenu(id)).thenReturn(Optional.of(menu));
				when(menuInfoMapper.from(menu)).thenReturn(mock(MenuInfo.Main.class));

				// when
				MenuInfo.Main result = menuService.getMenu(id);

				// then
				assertNotNull(result);
				verify(menuRepository).getMenu(id);
				verify(menuInfoMapper).from(menu);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailureCase {

			@Test
			@DisplayName("getMenu: 실패")
			void getMenu() {
				// given
				Integer id = 1;
				when(menuRepository.getMenu(id)).thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () -> menuService.getMenu(id));

				// then
				assertEquals(ErrorCode.NOT_FOUND_MENU.name(), exception.getCode());
				verify(menuRepository).getMenu(id);
				verifyNoInteractions(menuInfoMapper);
			}
		}
	}

	@Test
	@DisplayName("getMenus: 성공")
	void getMenus() {
		// given
		Boolean isActive = Boolean.TRUE;
		when(menuRepository.getMenus(isActive)).thenReturn(mock(List.class));
		when(menuInfoMapper.from(any(List.class))).thenReturn(mock(List.class));

		// when
		List<MenuInfo.Main> result = menuService.getMenus(isActive);

		// then
		assertNotNull(result);
		verify(menuRepository).getMenus(isActive);
		verify(menuInfoMapper).from(any(List.class));
	}

	@Nested
	@DisplayName("메뉴 생성")
	class CreateMenu {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("createMenu: 성공")
			void createMenu() {
				// given
				MenuCommand.Create command = mock(MenuCommand.Create.class);
				Menu menu = mock(Menu.class);
				when(command.getParentMenuId()).thenReturn(1);
				when(command.getLevel()).thenReturn(1);
				when(menuRepository.getMenu(1)).thenReturn(Optional.of(menu));
				when(menuRepository.saveMenu(command.toEntity())).thenReturn(menu);
				when(menuInfoMapper.from(any(Menu.class))).thenReturn(mock(MenuInfo.Main.class));

				// when
				MenuInfo.Main result = menuService.createMenu(command);

				// then
				assertNotNull(result);
				verify(menuRepository).saveMenu(command.toEntity());
				verify(menuInfoMapper).from(any(Menu.class));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("createMenu: 상위 메뉴가 없고 level은 1이 아닌 경우")
			void createMenu_상위_메뉴가_없고_level은_1이_아닌_경우() {
				// given
				MenuCommand.Create command = mock(MenuCommand.Create.class);
				when(command.getParentMenuId()).thenReturn(null);
				when(command.getLevel()).thenReturn(2);

				// when
				CustomException exception = assertThrows(CustomException.class, () -> menuService.createMenu(command));

				// then
				assertEquals(ErrorCode.INVALID_MENU_HIERARCHY.name(), exception.getCode());
				verifyNoInteractions(menuRepository);
				verifyNoInteractions(menuInfoMapper);
			}

			@Test
			@DisplayName("createMenu: 올바르지 않은 상위 메뉴 ID인 경우")
			void createMenu_올바르지_않은_상위_메뉴_ID인_경우() {
				// given
				MenuCommand.Create command = mock(MenuCommand.Create.class);
				when(command.getParentMenuId()).thenReturn(1);
				when(command.getLevel()).thenReturn(2);
				when(menuRepository.getMenu(1)).thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () -> menuService.createMenu(command));

				// then
				assertEquals(ErrorCode.NOT_FOUND_PARENT_MENU.name(), exception.getCode());
				verify(menuRepository).getMenu(1);
				verifyNoInteractions(menuInfoMapper);
			}

			@Test
			@DisplayName("createMenu: 상위 메뉴의 레벨이 등록하려는 메뉴의 레벨보다 낮은 경우")
			void createMenu_상위_메뉴의_레벨이_등록하려는_메뉴의_레벨보다_낮은_경우() {
				// given
				MenuCommand.Create command = mock(MenuCommand.Create.class);
				when(command.getParentMenuId()).thenReturn(1);
				when(command.getLevel()).thenReturn(2);
				Menu parentMenu = Menu.builder().level(3).build();
				when(menuRepository.getMenu(1)).thenReturn(Optional.of(parentMenu));

				// when
				CustomException exception = assertThrows(CustomException.class, () -> menuService.createMenu(command));

				// then
				assertEquals(ErrorCode.INVALID_PARENT_MENU_LEVEL.name(), exception.getCode());
				verify(menuRepository).getMenu(1);
				verifyNoInteractions(menuInfoMapper);
			}

		}
	}

	@Nested
	@DisplayName("메뉴 수정")
	class UpdateMenu {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("updateMenu: 성공")
			void updateMenu() {
				// given
				Integer id = 1;
				MenuCommand.Update command = mock(MenuCommand.Update.class);
				Menu menu = mock(Menu.class);
				when(menuRepository.getMenu(id)).thenReturn(Optional.of(menu));

				// when
				menuService.updateMenu(id, command);

				// then
				verify(menuRepository).getMenu(id);
				verify(menu).update(
					command.getMessageId(),
					command.getUrl(),
					command.getName(),
					command.getIsActive(),
					command.getDescription()
				);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("updateMenu: 존재하지 않는 메뉴 ID")
			void updateMenu() {
				// given
				Integer id = 1;
				MenuCommand.Update command = mock(MenuCommand.Update.class);
				when(menuRepository.getMenu(id)).thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class,
					() -> menuService.updateMenu(id, command));

				// then
				assertEquals(ErrorCode.NOT_FOUND_MENU.name(), exception.getCode());
				verify(menuRepository).getMenu(id);
			}
		}
	}

	@Nested
	@DisplayName("메뉴 위치 수정")
	class UpdateMenuPosition {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("updateMenuPositions: 성공")
			void updateMenuPositions() {
				// given
				MenuCommand.UpdatePosition command = mock(MenuCommand.UpdatePosition.class);
				Menu menu = mock(Menu.class);

				when(command.getLevel1()).thenReturn(List.of(1));
				when(command.getLevel2()).thenReturn(mock(Map.class));
				when(command.getLevel3()).thenReturn(mock(Map.class));
				when(menuRepository.getMenu(1)).thenReturn(Optional.of(menu));

				// when
				menuService.updateMenuPositions(command);

				// then
				verify(menuRepository).getMenu(1);
				verify(menu).update(null, 1, 1);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("updateMenuPositions: 존재하지 않는 메뉴 ID")
			void updateMenuPositions() {
				// given
				MenuCommand.UpdatePosition command = mock(MenuCommand.UpdatePosition.class);
				when(command.getLevel1()).thenReturn(List.of(1));
				when(menuRepository.getMenu(1)).thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class,
					() -> menuService.updateMenuPositions(command));

				// then
				assertEquals(ErrorCode.NOT_FOUND_MENU.name(), exception.getCode());
				verify(menuRepository).getMenu(1);
			}
		}
	}

	@Nested
	@DisplayName("메뉴 삭제")
	class DeleteMenu {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("deleteMenu: 성공")
			void deleteMenu() {
				// given
				Integer id = 1;
				Menu menu = mock(Menu.class);
				when(menuRepository.getMenu(id)).thenReturn(Optional.of(menu));

				// when
				menuService.deleteMenu(id);

				// then
				verify(menuRepository).getMenu(id);
				verify(menu).update(Boolean.TRUE);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("deleteMenu: 존재하지 않는 메뉴 ID")
			void deleteMenu() {
				// given
				Integer id = 1;
				when(menuRepository.getMenu(id)).thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () -> menuService.deleteMenu(id));

				// then
				assertEquals(ErrorCode.NOT_FOUND_MENU.name(), exception.getCode());
				verify(menuRepository).getMenu(id);
			}
		}
	}
}