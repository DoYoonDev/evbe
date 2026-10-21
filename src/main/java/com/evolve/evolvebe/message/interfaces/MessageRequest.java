package com.evolve.evolvebe.message.interfaces;

import lombok.*;

public class MessageRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetList {
        private String id;
        private String koText;
        private String enText;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Create {
        private String id;
        private String koText;
        private String enText;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String id;
        private String koText;
        private String enText;
        private Boolean isActive;
    }
}
