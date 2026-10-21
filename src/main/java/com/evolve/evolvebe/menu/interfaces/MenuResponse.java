package com.evolve.evolvebe.menu.interfaces;

import com.evolve.evolvebe.menu.domain.MenuInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MenuResponse {

    @Getter
    @Builder
    public static class Main {
        private MenuInfo.Main menuInfo;
    }

    @Getter
    @Builder
    public static class MenuList {
        private List<MenuInfo.Main> menuInfos;
    }

}
