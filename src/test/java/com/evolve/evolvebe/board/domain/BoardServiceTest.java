package com.evolve.evolvebe.board.domain;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardInfoMapper boardInfoMapper;

    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("getAllBoards: 성공")
    void getAllBoards() {
        List<Board> boards = List.of(mock(Board.class), mock(Board.class));
        List<BoardInfo.Summary> summaries = List.of(mock(BoardInfo.Summary.class), mock(BoardInfo.Summary.class));
        when(boardRepository.findAll()).thenReturn(boards);
        when(boardInfoMapper.mapToSummaryList(boards)).thenReturn(summaries);

        List<BoardInfo.Summary> result = boardService.getAllBoards();

        assertEquals(2, result.size());
        verify(boardRepository).findAll();
        verify(boardInfoMapper).mapToSummaryList(boards);
    }

    @Test
    @DisplayName("getAllBoardsPaged: 성공")
    void getAllBoardsPaged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Board> boards = new PageImpl<>(List.of(mock(Board.class)), pageable, 1);
        Page<BoardInfo.Summary> summaries = new PageImpl<>(List.of(mock(BoardInfo.Summary.class)), pageable, 1);

        when(boardRepository.findAllPaged(pageable)).thenReturn(boards);
        when(boardInfoMapper.mapToSummaryPage(boards)).thenReturn(summaries);
    }

    @Test
    @DisplayName("getBoardDetail: 성공")
    void getBoardDetail() {
        // given
        Long id = 1L;
        Board board = Board.builder().id(id).build();
        HttpSession session = mock(HttpSession.class);

        when(boardRepository.findWithAttachFilesById(id)).thenReturn(Optional.of(board));
        when(boardInfoMapper.mapToDetail(board)).thenReturn(mock(BoardInfo.Detail.class));

        // when
        BoardInfo.Detail result = boardService.getBoardDetail(id, session);

        // then
        assertNotNull(result);
        verify(boardRepository).findWithAttachFilesById(id);
        verify(boardInfoMapper).mapToDetail(board);
    }

    @Test
    @DisplayName("createBoard: 첨부파일 없이 게시글 생성")
    void createBoard() throws IOException {
        // given
        BoardCommand.Create command = mock(BoardCommand.Create.class);
        Board board = Board.builder().id(1L).build();
        BoardInfo.Detail createBoard = mock(BoardInfo.Detail.class);

        when(command.toEntity()).thenReturn(board);
        when(boardRepository.saveBoard(board)).thenReturn(board);
        when(boardInfoMapper.mapToDetail(board)).thenReturn(createBoard);

        // when
        BoardInfo.Detail result = boardService.createBoard(command, null);

        // then
        assertNotNull(result);
        assertEquals(createBoard, result);

        verify(boardRepository, atLeastOnce()).saveBoard(board);
        verify(boardInfoMapper).mapToDetail(board);
    }

    @Test
    @DisplayName("updateBoard: 성공")
    void updateBoard() throws IOException {
        Long id = 1L;
        BoardCommand.Update command = mock(BoardCommand.Update.class);
        when(command.getId()).thenReturn(id);
        when(command.getTitle()).thenReturn("게시글1 수정");
        when(command.getContent()).thenReturn("test 게시글 내용입니다.");
        when(command.getUpdateMemberId()).thenReturn("user1");
        when(command.getAttachFiles()).thenReturn(List.of()); // 비어있지만 null 아님
        when(command.getIsPin()).thenReturn(false);

        Board board = Board.builder().id(id).build();
        when(boardRepository.getBoard(id)).thenReturn(Optional.of(board));

        BoardInfo.Detail mapped = mock(BoardInfo.Detail.class);
        when(boardInfoMapper.mapToDetail(board)).thenReturn(mapped);

        BoardInfo.Detail result = boardService.updateBoard(command, null);

        assertEquals("게시글1 수정", board.getTitle());
        assertEquals("test 게시글 내용입니다.", board.getContent());
        assertEquals("user1", board.getUpdateMemberId());
        assertFalse(board.getIsPin());

        verify(boardRepository).saveBoard(board);
        verify(boardInfoMapper).mapToDetail(board);
        assertSame(mapped, result);
    }

    @Test
    @DisplayName("deleteBoard: 성공")
    void deleteBoard() {
        Long id = 1L;
        Board board = Board.builder().id(id).build();
        when(boardRepository.getBoard(id)).thenReturn(Optional.of(board));

        boardService.deleteBoard(id);

        verify(boardRepository).delete(board);
    }
}
