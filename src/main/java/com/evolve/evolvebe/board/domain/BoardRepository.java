package com.evolve.evolvebe.board.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    // 특정 게시글 조회
    Optional<Board> getBoard(Long boardId);

    Optional<Board> findWithAttachFilesById(Long boardId);

    // 조회수 업데이트
    void increaseViewCount(Long boardId);

    // 전체 게시글 조회
    List<Board> findAll();
    // 전체 목록 - 페이징
    Page<Board> findAllPaged(Pageable pageable);
    // 전체 목록 - 페이징 (컬럼별)
    Page<Board> findByAttachFileGroupIdContaining(String keyword, Pageable pageable);
    Page<Board> findByTitleContaining(String keyword, Pageable pageable);
    Page<Board> findByCreateMemberIdContaining(String keyword, Pageable pageable);

    Board saveBoard(Board board);

    // 게시글 삭제
    void delete(Board board);
}
