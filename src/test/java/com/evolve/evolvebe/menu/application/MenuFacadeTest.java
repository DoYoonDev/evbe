package com.evolve.evolvebe.menu.application;

import com.evolve.evolvebe.menu.domain.MenuCommand;
import com.evolve.evolvebe.menu.domain.MenuInfo;
import com.evolve.evolvebe.menu.domain.MenuService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuFacadeTest {

    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuFacade menuFacade;

    @Test
    @DisplayName("메뉴 ID로 조회")
    void getMenu() {
        // given
        Integer id = 1;
        MenuInfo.Main menuInfo = mock(MenuInfo.Main.class);
        when(menuService.getMenu(id)).thenReturn(menuInfo);

        // when
        MenuInfo.Main result = menuFacade.getMenu(id);

        // then
        assertEquals(menuInfo, result);
        verify(menuService).getMenu(id);
    }

    @Test
    @DisplayName("메뉴 목록 조회")
    void getMenus() {
        // given
        Boolean isActive = Boolean.TRUE;
        MenuInfo.Main menuInfo = mock(MenuInfo.Main.class);
        when(menuService.getMenus(isActive)).thenReturn(List.of(menuInfo));

        // when
        List<MenuInfo.Main> result = menuFacade.getMenus(isActive);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(menuService).getMenus(isActive);
    }

    @Test
    @DisplayName("메뉴 생성")
    void createMenu() {
        // given
        MenuInfo.Main menuInfo = mock(MenuInfo.Main.class);
        when(menuService.createMenu(any())).thenReturn(menuInfo);

        // when
        MenuInfo.Main result = menuFacade.createMenu(any());

        // then
        assertEquals(menuInfo, result);
        verify(menuService).createMenu(any());
    }

    @Test
    @DisplayName("메뉴 수정")
    void updateMenu() {
        // given
        Integer id = 1;
        MenuCommand.Update command = mock(MenuCommand.Update.class);

        // when
        menuFacade.updateMenu(id, command);

        // then
        verify(menuService).updateMenu(id, command);
    }

    @Test
    @DisplayName("메뉴 위치 수정")
    void updateMenuPositions() {
        // given
        MenuCommand.UpdatePosition command = mock(MenuCommand.UpdatePosition.class);

        // when
        menuFacade.updateMenuPositions(command);

        // then
        verify(menuService).updateMenuPositions(command);
    }

    @Test
    @DisplayName("메뉴 삭제")
    void deleteMenu() {
        // given
        Integer id = 1;

        // when
        menuFacade.deleteMenu(id);

        // then
        verify(menuService).deleteMenu(id);
    }
    
}