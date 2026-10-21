package com.evolve.evolvebe.menu.interfaces;

import lombok.*;

import java.util.List;
import java.util.Map;

public class MenuRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Get {
        private Integer id;
        @Builder.Default
        private String name = "";
        private Integer parentMenuId;
        @Builder.Default
        private Boolean isActive = true;
    }

    @Getter
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
    }

    @Getter
    public static class Update {
        private String messageId;
        private String url;
        private String name;
        private Boolean isActive;
        private String description;
    }

    @Getter
    public static class UpdatePosition {
        private List<Integer> level1;
        private Map<String, List<Integer>> level2;
        private Map<String, List<Integer>> level3;
    }
}

