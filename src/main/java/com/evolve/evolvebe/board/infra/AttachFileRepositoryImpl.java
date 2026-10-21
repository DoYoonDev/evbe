package com.evolve.evolvebe.board.infra;

import com.evolve.evolvebe.board.domain.AttachFile;
import com.evolve.evolvebe.board.domain.AttachFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AttachFileRepositoryImpl implements AttachFileRepository {

    private final AttachFileJpaRepository attachFileJpaRepository;

    // 첨부파일 저장
    @Override
    public AttachFile saveAttachFile(AttachFile attachFile) { return attachFileJpaRepository.save(attachFile); }

    @Override
    public List<AttachFile> saveAttachFileAll(List<AttachFile> attachFiles) { return attachFileJpaRepository.saveAll(attachFiles); }

    @Override
    public Optional<AttachFile> findByBoardIdAndSaveName(Long boardId, String saveName) {
        return attachFileJpaRepository.findByBoardIdAndSaveName(boardId, saveName);
    }

    @Override
    public List<AttachFile> findAllByBoardId(Long boardId){
        return attachFileJpaRepository.findAllByBoardId(boardId);
    }

}
