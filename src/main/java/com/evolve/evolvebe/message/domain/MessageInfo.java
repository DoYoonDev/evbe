package com.evolve.evolvebe.message.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;

public class MessageInfo {

    @Getter
    @Builder
    public static class Main {
        private final String id;
        private final String koText;
        private final String enText;
        private final Boolean isActive;
        private final String createMemberId;
        private final ZonedDateTime createdAt;
        private final String updateMemberId;
        private final ZonedDateTime updatedAt;
    }

    public static Message toEntity(
        String id,
        String koText,
        String enText,
        Boolean isActive
    ) {
        return Message.builder()
            .id(id)
            .koText(koText)
            .enText(enText)
            .isActive(isActive)
            .build();
    }

}