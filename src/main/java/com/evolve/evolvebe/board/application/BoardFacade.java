package com.evolve.evolvebe.board.application;

import com.evolve.evolvebe.board.domain.BoardCommand;
import com.evolve.evolvebe.board.domain.BoardInfo;
import com.evolve.evolvebe.board.domain.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardFacade {

    private final BoardService boardService;

    // 전체 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<BoardInfo.Summary> getAllBoards() { return boardService.getAllBoards(); }

    // 전체 게시글 목록 조회 (Page)
    @Transactional(readOnly = true)
    public Page<BoardInfo.Summary> getAllBoardsPaged(String keyword, String column, Pageable pageable) {
        return boardService.getAllBoardsPaged(column, keyword, pageable);
    }

    // 게시글 상세 조회
    @Transactional
    public BoardInfo.Detail getBoardDetail(long boardId, HttpSession session) {
        return boardService.getBoardDetail(boardId, session);
    }

    // 게시글 등록
    @Transactional
    public BoardInfo.Detail createBoard(BoardCommand.Create command, List<MultipartFile> files) throws IOException {
        return boardService.createBoard(command, files);
    }

    // 첨부파일
    @Transactional
    public UrlResource getFileResource(Long boardId, String saveName) throws MalformedURLException {
        return boardService.getFileResource(boardId, saveName);
    }

    // 게시글 수정
    @Transactional
    public BoardInfo.Detail updateBoard(BoardCommand.Update command, List<MultipartFile> attachFiles) throws IOException {
        return boardService.updateBoard(command, attachFiles);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(Long id) {
        boardService.deleteBoard(id);
    }
}
