package com.evolve.evolvebe.board.application;

import com.evolve.evolvebe.board.domain.BoardCommand;
import com.evolve.evolvebe.board.domain.BoardInfo;
import com.evolve.evolvebe.board.domain.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardFacadeTest {

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardFacade boardFacade;

    @Test
    @DisplayName("전체 게시글 목록 조회")
    void getAllboards() {
        List<BoardInfo.Summary> summaries = List.of(mock(BoardInfo.Summary.class));
        when(boardService.getAllBoards()).thenReturn(summaries);

        List<BoardInfo.Summary> result = boardFacade.getAllBoards();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(boardService).getAllBoards();
    }

    @Test
    @DisplayName("전체 게시글 목록 조회 (Page)")
    void getAllBoardPaged() {
        String keyword = "게시글1";
        String column = "title";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());
        Page<BoardInfo.Summary> page = new PageImpl<>(List.of(mock(BoardInfo.Summary.class)), pageable, 1);

        when(boardService.getAllBoardsPaged(eq(column), eq(keyword), any(Pageable.class)))
                .thenReturn(page);

        Page<BoardInfo.Summary> result = boardFacade.getAllBoardsPaged(keyword, column, pageable);

        assertEquals(1, result.getTotalElements());

        verify(boardService).getAllBoardsPaged(eq(column), eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("게시글 등록")
    void createBoard() throws IOException {
        // given
        BoardCommand.Create command = mock(BoardCommand.Create.class);
        BoardInfo.Detail board = mock(BoardInfo.Detail.class);
        when(boardService.createBoard(eq(command), isNull())).thenReturn(board);

        // when
        BoardInfo.Detail result = boardFacade.createBoard(command, null);

        // then
        assertSame(board, result);
        verify(boardService).createBoard(eq(command), isNull());
    }

    @Test
    @DisplayName("게시글 수정")
    void updateBoard() throws IOException {
        // given
        BoardCommand.Update command = mock(BoardCommand.Update.class);
        BoardInfo.Detail board = mock(BoardInfo.Detail.class);
        when(boardService.updateBoard(eq(command), isNull())).thenReturn(board);

        BoardInfo.Detail result = boardFacade.updateBoard(command, null);

        assertSame(board, result);
        verify(boardService).updateBoard(eq(command), isNull());
    }

    @Test
    @DisplayName("게시글 삭제")
    void deleteBoard() {
        // given
        Long id = 1L;

        // when
        boardFacade.deleteBoard(id);

        // then
        verify(boardService).deleteBoard(id);
    }
}
