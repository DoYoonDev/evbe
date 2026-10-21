package com.evolve.evolvebe.board;

import com.evolve.evolvebe.board.application.BoardFacade;
import com.evolve.evolvebe.board.domain.BoardCommand;
import com.evolve.evolvebe.board.domain.BoardInfo;
import com.evolve.evolvebe.board.domain.BoardRepository;
import com.evolve.evolvebe.board.interfaces.BoardDtoMapper;
import com.evolve.evolvebe.board.interfaces.BoardRequest;
import com.evolve.evolvebe.board.interfaces.BoardResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@ActiveProfiles("test")
public class BoardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BoardRepository boardRepository;

    @MockitoBean
    private BoardFacade boardFacade;

    @MockitoBean
    private BoardDtoMapper boardDtoMapper;

    @Test
    @DisplayName("GET /api/boards - 전체 게시글 조회")
    void getAllBoards_ok() throws Exception {
        List<BoardInfo.Summary> sumaries = List.of(
                BoardInfo.Summary.builder()
                        .title("게시글1")
                        .attachFileGroupId(null)
                        .createMemberId("user1")
                        .createdAt(ZonedDateTime.now())
                        .viewCount("0")
                        .isPin(false)
                        .build(),
                BoardInfo.Summary.builder()
                        .title("게시글2")
                        .attachFileGroupId(null)
                        .createMemberId("user1")
                        .createdAt(ZonedDateTime.now())
                        .viewCount("0")
                        .isPin(false)
                        .build(),
                BoardInfo.Summary.builder()
                        .title("게시글3")
                        .attachFileGroupId(null)
                        .createMemberId("user2")
                        .createdAt(ZonedDateTime.now())
                        .viewCount("0")
                        .isPin(false)
                        .build(),
                BoardInfo.Summary.builder()
                        .title("게시글4")
                        .attachFileGroupId(null)
                        .createMemberId("user2")
                        .createdAt(ZonedDateTime.now())
                        .viewCount("5")
                        .isPin(false)
                        .build(),
                BoardInfo.Summary.builder()
                        .title("게시글5")
                        .attachFileGroupId(null)
                        .createMemberId("user3")
                        .createdAt(ZonedDateTime.now())
                        .viewCount("4")
                        .isPin(false)
                        .build(),
                BoardInfo.Summary.builder()
                        .title("게시글6")
                        .attachFileGroupId(null)
                        .createMemberId("user3")
                        .createdAt(ZonedDateTime.now())
                        .viewCount("0")
                        .isPin(false)
                        .build()
        );
        BoardResponse.BoardListResponse response =
                BoardResponse.BoardListResponse.builder().boardInfos(sumaries).build();

        when(boardFacade.getAllBoards()).thenReturn(sumaries);
        when(boardDtoMapper.mapToBoardList(sumaries)).thenReturn(response);

        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.boardInfos").isArray())
                .andExpect(jsonPath("$.boardInfos.length()").value(6))
                .andExpect(jsonPath("$.boardInfos[0].title").value("게시글1"));
    }

    @Test
    @DisplayName("GET /api/boards - 페이징 조회")
    void getBoardsPaged_ok() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BoardInfo.Summary> boardPage = new PageImpl<>(
                List.of(
                        BoardInfo.Summary.builder()
                                .title("게시글1")
                                .createMemberId("user1")
                                .createdAt(ZonedDateTime.now())
                                .viewCount("0")
                                .isPin(false)
                                .build(),
                        BoardInfo.Summary.builder()
                                .title("게시글2")
                                .createMemberId("user1")
                                .createdAt(ZonedDateTime.now())
                                .viewCount("0")
                                .isPin(false)
                                .build(),
                        BoardInfo.Summary.builder()
                                .title("게시글3")
                                .createMemberId("user2")
                                .createdAt(ZonedDateTime.now())
                                .viewCount("0")
                                .isPin(false)
                                .build(),
                        BoardInfo.Summary.builder()
                                .title("게시글4")
                                .createMemberId("user2")
                                .createdAt(ZonedDateTime.now())
                                .viewCount("5")
                                .isPin(false)
                                .build(),
                        BoardInfo.Summary.builder()
                                .title("게시글5")
                                .createMemberId("user3")
                                .createdAt(ZonedDateTime.now())
                                .viewCount("4")
                                .isPin(false)
                                .build(),
                        BoardInfo.Summary.builder()
                                .title("게시글6")
                                .createMemberId("user3")
                                .createdAt(ZonedDateTime.now())
                                .viewCount("0")
                                .isPin(false)
                                .build()
                ),
                pageable,
                6
        );

        BoardResponse.BoardListResponse response =
                BoardResponse.BoardListResponse.builder()
                        .boardInfos(boardPage.getContent())
                        .build();

        when(boardFacade.getAllBoardsPaged(eq("게시글1"), eq("title"), any(Pageable.class)))
                .thenReturn(boardPage);
        when(boardDtoMapper.mapToBoardList(boardPage.getContent()))
                .thenReturn(response);

        mockMvc.perform(get("/api/boards/page")
                        .param("column", "title")
                        .param("keyword", "게시글1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt, desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfos").exists())
                .andExpect(jsonPath("$.boardInfos.length()").value(6))
                .andExpect(jsonPath("$.boardInfos[0].title").value("게시글1"));
    }

    @Test
    @DisplayName("GET /api/boards/{id} - 게시글 상세 조회")
    void getBoardById_ok() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        Long id = 1L;

        BoardInfo.Detail detail = BoardInfo.Detail.builder()
                .id(id)
                .title("게시글1")
                .content("test 게시글 내용입니다.")
                .createMemberId("user1")
                .attachFileGroupId(null)
                .createdAt(now)
                .updatedAt(now)
                .viewCount(0)
                .isPin(false)
                .build();

        BoardResponse.BoardDetailResponse response = BoardResponse.BoardDetailResponse.builder()
                .id(detail.getId())
                .title(detail.getTitle())
                .content(detail.getContent())
                .createMemberId(detail.getCreateMemberId())
                .attachFileGroupId(detail.getAttachFileGroupId())
                .viewCount(detail.getViewCount())
                .isPin(detail.getIsPin())
                .build();

        when(boardFacade.getBoardDetail(eq(id), any())).thenReturn(detail);
        when(boardDtoMapper.toDetailResponse(detail)).thenReturn(response);

        mockMvc.perform(get("/api/boards/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("게시글1"))
                .andExpect(jsonPath("$.content").value("test 게시글 내용입니다."))
                .andExpect(jsonPath("$.createMemberId").value("user1"))
                .andExpect(jsonPath("$.attachFileGroupId").isEmpty()) // null 안전 처리
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.isPin").value(false));
    }

    @Test
    @DisplayName("POST /api/boards - 게시글 등록 성공")
    void createBoard_ok() throws Exception {
        var req = BoardRequest.Create.builder()
                .content("테스트 게시글 내용입니다.")
                .title("게시글7")
                .createMemberId("user1")
                .attachFiles(null)
                .isPin(false)
                .build();

        var createCmd = BoardCommand.Create.builder()
                .content("테스트 게시글 내용입니다.")
                .title("게시글7")
                .createMemberId("user1")
                .attachFiles(null)
                .isPin(false)
                .build();

        var createBoard = BoardInfo.Detail.builder()
                .id(7L)
                .attachFileGroupId(null)
                .content("테스트 게시글 내용입니다.")
                .title("게시글7")
                .createMemberId("user1")
                .updateMemberId("user1")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .viewCount(0)
                .isPin(false)
                .build();

        when(boardDtoMapper.toCreateCommand(any(BoardRequest.Create.class), isNull())).thenReturn(createCmd);
        when(boardFacade.createBoard(eq(createCmd), isNull())).thenReturn(createBoard);

        MockMultipartFile boardJson = new MockMultipartFile(
                "board",
                "board.json",
                "application/json",
                mapper.writeValueAsBytes(req)
        );

        mockMvc.perform(multipart("/api/boards")
                        .file(boardJson))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("PUT /api/boards/{id} - 게시글 수정")
    void updateBoard_ok() throws Exception {
        Long id = 1L;

        var req = BoardRequest.Update.builder()
                .content("수정된 게시글 내용입니다.")
                .title("게시글1 수정")
                .updateMemberId("user1")
                .isPin(false)
                .build();

        var updateCmd = BoardCommand.Update.builder()
                .id(id)
                .content(req.getContent())
                .title(req.getTitle())
                .updateMemberId(req.getUpdateMemberId())
                .isPin(req.getIsPin())
                .build();

        var updatedBoard = BoardInfo.Detail.builder()
                .id(id)
                .attachFileGroupId(null)
                .content(req.getContent())
                .title(req.getTitle())
                .createMemberId("user1")
                .updateMemberId("user1")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .viewCount(0)
                .isPin(false)
                .build();

        when(boardDtoMapper.toUpdateCommand(eq(id), any(BoardRequest.Update.class), isNull()))
                .thenReturn(updateCmd);
        when(boardFacade.updateBoard(eq(updateCmd), isNull()))
                .thenReturn(updatedBoard);
        when(boardDtoMapper.toUpdateResponse(any(BoardInfo.Detail.class)))
                .thenReturn(BoardResponse.BoardUpdateResponse.builder()
                        .id(updatedBoard.getId())
                        .content(updatedBoard.getContent())
                        .title(updatedBoard.getTitle())
                        .updateMemberId(updatedBoard.getUpdateMemberId())
                        .attachFileGroupId(updatedBoard.getAttachFileGroupId())
                        .isPin(updatedBoard.getIsPin())
                        .build()
                );

        MockMultipartFile boardJson = new MockMultipartFile(
                "board",
                "",
                "application/json",
                mapper.writeValueAsBytes(req)
        );

        MockHttpServletRequestBuilder builder = multipart("/api/boards/{id}", id)
                .file(boardJson)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                });

        mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.content").value(req.getContent()))
                .andExpect(jsonPath("$.title").value(req.getTitle()))
                .andExpect(jsonPath("$.updateMemberId").value(req.getUpdateMemberId()))
                .andExpect(jsonPath("$.attachFileGroupId").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.isPin").value(false));
    }

    @Test
    @DisplayName("DELETE /api/boards/{id} - 게시글 삭제")
    void deleteBoard() throws Exception {
        Mockito.doNothing().when(boardFacade).deleteBoard(1L);

        mockMvc.perform(delete("/api/boards/{id}", 1L))
                .andExpect(status().isNoContent());
    }

}
