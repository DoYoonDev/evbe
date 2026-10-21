package com.evolve.evolvebe.board.infra;

import com.evolve.evolvebe.board.domain.AttachFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttachFileJpaRepository extends JpaRepository<AttachFile, Long> {

    Optional<AttachFile> findByBoardIdAndSaveName(Long boardId, String saveName);

    List<AttachFile> findAllByBoardId(Long boardId);
}
