package com.evolve.evolvebe.menu.domain;

import lombok.Builder;
import lombok.Getter;

public class MenuInfo {

    @Getter
    @Builder
    public static class Main {
        private final Integer id;
        private final Integer parentMenuId;
        private final String messageId;
        private final String url;
        private final String name;
        private final Boolean isActive;
        private final Boolean isDelete;
        private final Integer sortOrder;
        private final String description;
        private final Integer level;
        private final String createMemberId;
        private final String updateMemberId;
    }
}
