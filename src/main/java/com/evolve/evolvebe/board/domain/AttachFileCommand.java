package com.evolve.evolvebe.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class AttachFileCommand {

    @Getter
    @Setter
    @Builder
    public static class Create {
        private String name;
        private String saveName;
        private String path;
        private String extension;
        private String size;
        private String createMemberId;
        private String updateMemberId;

        public AttachFile toEntity(String userId) {
            return AttachFile.builder()
                    .name(name)
                    .saveName(saveName)
                    .path(path)
                    .extension(extension)
                    .createMemberId(userId)
                    .updateMemberId(userId)
                    .size(size)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class Update {
        private String name;
        private String saveName;
        private String path;
        private String extension;
        private String size;
        private String updateMemberId;

        public AttachFile toEntity(String userId) {
            return AttachFile.builder()
                    .name(name)
                    .saveName(saveName)
                    .path(path)
                    .extension(extension)
                    .updateMemberId(userId)
                    .size(size)
                    .build();
        }
    }
}
