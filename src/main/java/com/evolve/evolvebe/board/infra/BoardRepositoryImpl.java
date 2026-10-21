package com.evolve.evolvebe.board.infra;

import com.evolve.evolvebe.board.domain.Board;
import com.evolve.evolvebe.board.domain.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepository {
    
    private final BoardJpaRepository boardJpaRepository;

    // 특정 게시글 조회
    @Override
    public Optional<Board> getBoard(Long boardId) { return boardJpaRepository.findById(String.valueOf(boardId)); }

    @Override
    public Optional<Board> findWithAttachFilesById(Long boardId) { return boardJpaRepository.findWithAttachFilesById(boardId); }

    // 조회수 업데이트
    @Override
    public void increaseViewCount(Long boardId) {
        boardJpaRepository.increaseViewCount(boardId);
    }
    
    // 전체 게시글 조회
    @Override
    public List<Board> findAll() { return boardJpaRepository.findAll(); }

    // 전체 게시글 조회 (page)
    @Override
    public Page<Board> findAllPaged(Pageable pageable) { return boardJpaRepository.findAll(pageable); }

    // 게시글 검색
    @Override
    public Page<Board> findByAttachFileGroupIdContaining(String keyword, Pageable pageable) {
        return boardJpaRepository.findByAttachFileGroupIdContaining(keyword, pageable);
    }

    @Override
    public Page<Board> findByTitleContaining(String keyword, Pageable pageable) {
        return boardJpaRepository.findByTitleContaining(keyword, pageable);
    }

    @Override
    public Page<Board> findByCreateMemberIdContaining(String keyword, Pageable pageable) {
        return boardJpaRepository.findByCreateMemberIdContaining(keyword, pageable);
    }

    @Override
    public Board saveBoard(Board board) { return boardJpaRepository.save(board); }

    // 게시글 삭제
    @Override
    public void delete(Board board) { boardJpaRepository.delete(board); }
}
