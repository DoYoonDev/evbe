package com.evolve.evolvebe.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

public class BoardInfo {

    // 외부 목록
    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    public static class Summary {
        private final Long id;
        private final Long attachFileGroupId;
        private final String title;
        private final String createMemberId;
        private final ZonedDateTime createdAt;
        private final String viewCount;
        private final Boolean isPin;
    }

    // 게시글 상세
    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    public static class Detail {
        private final Long id;
        private final Long attachFileGroupId;
        private final String content;
        private final String title;
        private final String createMemberId;
        private final String updateMemberId;
        private final ZonedDateTime createdAt;
        private final ZonedDateTime updatedAt;
        private final int viewCount;
        private final Boolean isPin;

        private final List<AttachFileInfo> attachFiles;

        @Getter
        @Builder
        @AllArgsConstructor
        public static class AttachFileInfo {
            private final Long id;
            private final String name;
            private final String saveName;
            private final String path;
        }
    }
}
