package com.evolve.evolvebe.message.domain;

import lombok.Builder;
import lombok.Getter;

public class MessageCommand {

    @Getter
    @Builder
    public static class GetList {
        private String id;
        private String koText;
        private String enText;
        private Boolean isActive;
    }

    @Getter
    @Builder
    public static class Create {
        private String id;
        private String koText;
        private String enText;
        private Boolean isActive;

        public Message toEntity() {
            return Message.builder()
                .id(id)
                .koText(koText)
                .enText(enText)
                .isActive(isActive)
                .build();
        }
    }

    @Getter
    @Builder
    public static class Update {
        private String koText;
        private String enText;
        private Boolean isActive;
    }

}
