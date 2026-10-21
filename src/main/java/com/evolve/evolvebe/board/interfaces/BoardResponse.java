package com.evolve.evolvebe.board.interfaces;

import com.evolve.evolvebe.board.domain.BoardInfo;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.ZonedDateTime;
import java.util.List;

public class BoardResponse {

    // 게시글 목록
    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardListResponse {
        private List<BoardInfo.Summary> boardInfos;

        public static BoardListResponse from(List<BoardInfo.Summary> boards) {
            return BoardListResponse.builder()
                    .boardInfos(boards)
                    .build();
        }
    }

    // 페이징 게시글 목록
    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BoardPageResponse {
        private List<BoardInfo.Summary> boardInfos;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public static BoardPageResponse from(Page<BoardInfo.Summary> pageData) {
            return BoardPageResponse.builder()
                    .boardInfos(pageData.getContent())
                    .page(pageData.getNumber())
                    .size(pageData.getSize())
                    .totalElements(pageData.getTotalElements())
                    .totalPages(pageData.getTotalPages())
                    .build();
        }
    }

    // 특정 게시글 조회
    @Getter
    @Builder
    public static class BoardDetailResponse {
        private Long id;
        private Long attachFileGroupId;
        private String content;
        private String title;
        private String createMemberId;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private int viewCount;
        private Boolean isPin;

        // 첨부파일 리스트
        private List<AttachFileResponse> attachFiles;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class AttachFileResponse {
            private Long id;
            private String name;
            private String saveName;
            private String path;
        }
    }

    // 게시글 수정
    @Getter
    @Builder
    @ToString
    public static class BoardUpdateResponse {
        private Long id;
        private Long attachFileGroupId;
        private String content;
        private String title;
        private String createMemberId;
        private String updateMemberId;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
        private int viewCount;
        private Boolean isPin;
    }
}
