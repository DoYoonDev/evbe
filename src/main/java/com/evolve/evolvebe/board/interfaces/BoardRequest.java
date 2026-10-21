package com.evolve.evolvebe.board.interfaces;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BoardRequest {

    @Getter
    @Setter
    @Builder
    public static class Create {
        private String content;
        private String title;
        private String createMemberId;

        // 첨부파일 정보 (optional)
        private List<AttachFileRequest> attachFiles;

        private Boolean isPin;
    }

    @Getter
    @Builder
    public static class AttachFileRequest {
        private String name;
        private String saveName;
        private String path;
        private String extension;
    }

    @Getter
    @Builder
    public static class Update {
        private Long attachFileGroupId;
        private String content;
        private String title;
        private String updateMemberId;
        private Boolean isPin;
    }
}

