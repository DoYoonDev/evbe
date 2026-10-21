package com.evolve.evolvebe.code.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mapstruct.Mapping;

public class CodeCommand {
    
    @Getter
    @Setter
    @ToString
    public static class GetCodeList {
        private String id;
        private String name;
        private String description;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @ToString
    public static class GetCodeGroupList {
        private String id;
        private String messageId;
        private String name;
        private String description;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @ToString
    public static class CreateCode {
        private String id;
        private String messageId;
        private String name;
        private String description;
        private Integer sortOrder;
        private Boolean isActive;

        @Mapping(target = "createMemberId", ignore = true)
        @Mapping(target = "updateMemberId", ignore = true)
        public Code toEntity(CodeGroup codeGroup) {
            return Code.builder()
                .id(id)
                .codeGroup(codeGroup)
                .messageId(messageId)
                .name(name)
                .description(description)
                .sortOrder(sortOrder)
                .isActive(isActive)
                .build();
        }
    }

    @Getter
    @Setter
    @ToString
    public static class CreateCodeGroup {
        private String id;
        private String name;
        private String description;
        private Boolean isActive;

        @Mapping(target = "createMemberId", ignore = true)
        @Mapping(target = "updateMemberId", ignore = true)
        public CodeGroup toEntity() {
            return CodeGroup.builder()
                .id(id)
                .name(name)
                .description(description)
                .isActive(isActive)
                .build();
        }
    }

    @Getter
    @Setter
    @ToString
    public static class UpdateCode {
        String name;
        String description;
        Integer sortOrder;
        Boolean isActive;
    }

    @Getter
    @Setter
    @ToString
    public static class UpdateCodeGroup {
        String name;
        String description;
        Boolean isActive;
    }

}
