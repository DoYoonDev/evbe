package com.evolve.evolvebe.code.interfaces;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class CodeRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetCodeList {
        private String id = "";
        private String name = "";
        private String description = "";
        private Boolean isActive = true;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetCodeGroupList {
        private String id = "";
        private String messageId = "";
        private String name = "";
        private String description = "";
        private Boolean isActive = true;
    }

    @Getter
    @Setter
    @ToString
    public static class CreateCode {
        private String id;
        private String groupId;
        private String messageId;
        private String name;
        private String description;
        private Integer sortOrder;
        @JsonProperty("isActive")
        private Boolean isActive;
    }

    @Getter
    @Setter
    @ToString
    public static class CreateCodeGroup {
        private String id;
        private String name;
        private String description;
        @JsonProperty("isActive")
        private Boolean isActive;
    }

    @Getter
    @Setter
    @ToString
    public static class UpdateCode {
        private String name;
        private String description;
        private Integer sortOrder;
        @JsonProperty("isActive")
        private Boolean isActive;
    }

    @Getter
    @Setter
    @ToString
    public static class UpdateCodeGroup {
        private String name;
        private String description;
        @JsonProperty("isActive")
        private Boolean isActive;
    }

}
