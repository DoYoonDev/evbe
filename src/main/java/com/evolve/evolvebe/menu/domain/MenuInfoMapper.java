package com.evolve.evolvebe.menu.domain;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface MenuInfoMapper {

	MenuInfo.Main from(Menu menu);

	List<MenuInfo.Main> from(List<Menu> menuList);

}
