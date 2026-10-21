package com.evolve.evolvebe.menu.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class MenuCommand {

    @Getter
    @Setter
    @Builder
    public static class Create {
        private Integer parentMenuId;
        private String messageId;
        private String url;
        private String name;
        private Boolean isActive;
        private Boolean isDelete;
        private Integer sortOrder;
        private String description;
        private Integer level;

        public Menu toEntity() {
            return Menu.builder()
                .parentMenuId(parentMenuId)
                .messageId(messageId)
                .url(url)
                .name(name)
                .isActive(isActive)
                .isDelete(isDelete)
                .sortOrder(sortOrder)
                .description(description)
                .level(level)
                .build();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class Update {
        private Integer parentMenuId;
        private String messageId;
        private String url;
        private String name;
        private Boolean isActive;
        private String description;

        public Menu toEntity() {
            return Menu.builder()
                .parentMenuId(parentMenuId)
                .messageId(messageId)
                .url(url)
                .name(name)
                .isActive(isActive)
                .description(description)
                .build();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class UpdatePosition {
        private List<Integer> level1;
        private Map<String, List<Integer>> level2;
        private Map<String, List<Integer>> level3;
    }
}
