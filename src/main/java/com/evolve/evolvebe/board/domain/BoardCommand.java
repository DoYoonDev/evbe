package com.evolve.evolvebe.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class BoardCommand {

    @Getter
    @Setter
    @Builder
    public static class Create {
        private String content;
        private String title;
        private String createMemberId;
        private List<AttachFileCommand.Create> attachFiles;
        private Boolean isPin;

        public Board toEntity() {
            Board board = Board.builder()
                    .content(content)
                    .title(title)
                    .createMemberId(createMemberId)
                    .updateMemberId(createMemberId)
                    .viewCount(0)
                    .isPin(isPin)
                    .build();

            if (attachFiles != null) {
                for (AttachFileCommand.Create fileCmd : attachFiles) {
                    AttachFile attachFile = fileCmd.toEntity(createMemberId);
                    board.addAttachFile(attachFile);
                }
            }

            return board;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class Update {
        private Long id;
        private String content;
        private String title;
        private String updateMemberId;
        private List<AttachFileCommand.Update> attachFiles;
        private Boolean isPin;
    }
}
