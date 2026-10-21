package com.evolve.evolvebe.menu.interfaces;

import com.evolve.evolvebe.menu.domain.MenuCommand;
import com.evolve.evolvebe.menu.domain.MenuInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
    componentModel = "spring",
    injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface MenuDtoMapper {

    MenuResponse.Main from(MenuInfo.Main menuInfo);

    default MenuResponse.MenuList from(List<MenuInfo.Main> menuInfos) {
        return MenuResponse.MenuList.builder()
            .menuInfos(menuInfos)
            .build();
    }

    MenuCommand.Create from(MenuRequest.Create request);

    @Mapping(target = "parentMenuId", ignore = true)
    MenuCommand.Update from(MenuRequest.Update request);

    MenuCommand.UpdatePosition from(MenuRequest.UpdatePosition request);

}
