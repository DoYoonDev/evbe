package com.evolve.evolvebe.board.domain;

import java.util.List;
import java.util.Optional;

public interface AttachFileRepository {

    AttachFile saveAttachFile(AttachFile attachFile);
    List<AttachFile> saveAttachFileAll(List<AttachFile> attachFiles);

    Optional<AttachFile> findByBoardIdAndSaveName(Long boardId, String saveName);

    List<AttachFile> findAllByBoardId(Long boardId);
}
