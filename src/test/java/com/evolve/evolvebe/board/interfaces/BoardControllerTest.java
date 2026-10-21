package com.evolve.evolvebe.board.interfaces;


import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.board.application.BoardFacade;
import com.evolve.evolvebe.board.domain.BoardCommand;
import com.evolve.evolvebe.board.domain.BoardInfo;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.ZonedDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BoardController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {SecurityConfig.class, JwtFilter.class}
                )
        }
)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BoardFacade boardFacade;

    @MockitoBean
    private BoardDtoMapper boardDtoMapper;

    @Test
    @DisplayName("전체 게시글 조회")
    void getAllBoard() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();

        List<BoardInfo.Summary> summaries = List.of(
            BoardInfo.Summary.builder()
                    .id(1L)
                    .attachFileGroupId(null)
                    .title("게시글1")
                    .createMemberId("user1")
                    .createdAt(now)
                    .viewCount("0")
                    .isPin(false)
                    .build(),
            BoardInfo.Summary.builder()
                    .id(2L)
                    .attachFileGroupId(null)
                    .title("게시글2")
                    .createMemberId("user1")
                    .createdAt(now)
                    .viewCount("5")
                    .isPin(false)
                    .build(),
            BoardInfo.Summary.builder()
                    .id(3L)
                    .attachFileGroupId(null)
                    .title("게시글3")
                    .createMemberId("user1")
                    .createdAt(now)
                    .viewCount("2")
                    .isPin(true)
                    .build(),
            BoardInfo.Summary.builder()
                    .id(4L)
                    .attachFileGroupId(null)
                    .title("게시글4")
                    .createMemberId("user1")
                    .createdAt(now)
                    .viewCount("0")
                    .isPin(false)
                    .build(),
            BoardInfo.Summary.builder()
                    .id(5L)
                    .attachFileGroupId(null)
                    .title("게시글5")
                    .createMemberId("user1")
                    .createdAt(now)
                    .viewCount("10")
                    .isPin(false)
                    .build(),
            BoardInfo.Summary.builder()
                    .id(6L)
                    .attachFileGroupId(null)
                    .title("게시글6")
                    .createMemberId("user1")
                    .createdAt(now)
                    .viewCount("2")
                    .isPin(true)
                    .build()
        );
        BoardResponse.BoardListResponse response = BoardResponse.BoardListResponse.builder().boardInfos(summaries).build();

        when(boardFacade.getAllBoards()).thenReturn(summaries);
        when(boardDtoMapper.mapToBoardList(summaries)).thenReturn(response);

        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfos").isArray())
                .andExpect(jsonPath("$.boardInfos.length()").value(6))
                .andExpect(jsonPath("$.boardInfos[0].id").value(1L))
                .andDo(print())
                .andDo(document("board/get-all",
                        resource(ResourceSnippetParameters.builder()
                                .tag("board")
                                .responseFields(
                                        fieldWithPath("boardInfos").description("게시글 요약 목록"),
                                        fieldWithPath("boardInfos[].id").description("게시글 ID"),
                                        fieldWithPath("boardInfos[].attachFileGroupId").description("첨부파일"),
                                        fieldWithPath("boardInfos[].title").description("제목"),
                                        fieldWithPath("boardInfos[].createMemberId").description("작성자"),
                                        fieldWithPath("boardInfos[].createdAt").description("작성일"),
                                        fieldWithPath("boardInfos[].viewCount").description("조회수"),
                                        fieldWithPath("boardInfos[].isPin").description("상단 고정 여부")
                                ).build()
                        )
                ));
    }

    @Test
    @DisplayName("전체 게시글 조회 (페이징)")
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
                .andExpect(jsonPath("$.boardInfos[0].title").value("게시글1"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andDo(document("board/get-boards-paged",
                        resource(ResourceSnippetParameters.builder()
                                .tag("board")
                                .queryParameters(
                                        parameterWithName("column").description("검색 기준 컬럼명 (예: title, createMemberId)"),
                                        parameterWithName("keyword").description("검색 키워드"),
                                        parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                        parameterWithName("size").description("한 페이지당 게시글 개수"),
                                        parameterWithName("sort").description("정렬 기준 (예: createdAt,desc)")
                                )
                                .responseFields(
                                        fieldWithPath("boardInfos").description("게시글 목록"),
                                        fieldWithPath("boardInfos[].id").description("게시글 ID").optional(),
                                        fieldWithPath("boardInfos[].attachFileGroupId").description("첨부파일 그룹 ID").optional(),
                                        fieldWithPath("boardInfos[].title").description("게시글 제목"),
                                        fieldWithPath("boardInfos[].createMemberId").description("작성자"),
                                        fieldWithPath("boardInfos[].createdAt").description("작성일시"),
                                        fieldWithPath("boardInfos[].viewCount").description("조회수"),
                                        fieldWithPath("boardInfos[].isPin").description("상단 고정 여부"),
                                        fieldWithPath("page").description("현재 페이지 번호"),
                                        fieldWithPath("size").description("한 페이지당 게시글 수"),
                                        fieldWithPath("totalElements").description("전체 게시글 개수"),
                                        fieldWithPath("totalPages").description("전체 페이지 수")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("게시글 상세 조회")
    void getBoardById_ok() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        List<BoardInfo.Detail.AttachFileInfo> attachFileInfos = List.of(
                BoardInfo.Detail.AttachFileInfo.builder()
                        .id(1L)
                        .name("test.txt")
                        .saveName("test_123.txt")
                        .path("/upload/files")
                        .build(),
                BoardInfo.Detail.AttachFileInfo.builder()
                        .id(1L)
                        .name("image.png")
                        .saveName("img_456.png")
                        .path("/upload/images")
                        .build()

        );

        BoardInfo.Detail board = BoardInfo.Detail.builder()
                .id(1L)
                .attachFileGroupId(1L)
                .content("테스트 게시글 내용입니다.")
                .title("게시글1")
                .createMemberId("user1")
                .updateMemberId("user1")
                .createdAt(now)
                .updatedAt(now)
                .viewCount(0)
                .isPin(false)
                .attachFiles(attachFileInfos)
                .build();

        BoardResponse.BoardDetailResponse response = BoardResponse.BoardDetailResponse.builder()
                .id(board.getId())
                .attachFileGroupId(board.getAttachFileGroupId())
                .content(board.getContent())
                .title(board.getTitle())
                .createMemberId(board.getCreateMemberId())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .viewCount(board.getViewCount())
                .isPin(board.getIsPin())
                .attachFiles(board.getAttachFiles().stream()
                        .map(f -> BoardResponse.BoardDetailResponse.AttachFileResponse.builder()
                                .id(f.getId())
                                .name(f.getName())
                                .saveName(f.getSaveName())
                                .path(f.getPath())
                                .build())
                        .toList())
                .build();

        when(boardFacade.getBoardDetail(eq(1L), any(HttpSession.class))).thenReturn(board);
        when(boardDtoMapper.toDetailResponse(board)).thenReturn(response);

        mockMvc.perform(get("/api/boards/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("게시글1"))
                .andExpect(jsonPath("$.attachFiles[0].saveName").value("test_123.txt"))
                .andDo(print())
                .andDo(document("board/get-board",
                        resource(ResourceSnippetParameters.builder()
                                        .tag("board")
                                        .pathParameters(
                                                RequestDocumentation.parameterWithName("id").description("게시글 ID")
                                        )
                                        .responseFields(
                                                fieldWithPath("id").description("게시글 ID"),
                                                fieldWithPath("attachFileGroupId").description("첨부파일 그룹 ID"),
                                                fieldWithPath("content").description("게시글 내용"),
                                                fieldWithPath("title").description("제목"),
                                                fieldWithPath("createMemberId").description("작성자"),
                                                fieldWithPath("createdAt").description("작성일"),
                                                fieldWithPath("updatedAt").description("수정일"),
                                                fieldWithPath("viewCount").description("조회수"),
                                                fieldWithPath("isPin").description("상단 고정 여부"),
                                                fieldWithPath("attachFiles").description("첨부파일 목록"),
                                                fieldWithPath("attachFiles[].id").description("첨부파일 ID"),
                                                fieldWithPath("attachFiles[].name").description("첨부파일명"),
                                                fieldWithPath("attachFiles[].saveName").description("저장 파일명"),
                                                fieldWithPath("attachFiles[].path").description("첨부파일 경로")
                                        ).build()
                                )
                ));
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
                .andExpect(content().string("true"))
                .andDo(document("board/create",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("board")
                                        .build()
                        )
                ));
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
                .andExpect(jsonPath("$.isPin").value(false))
                .andDo(document("board/update-board",
                        resource(ResourceSnippetParameters.builder()
                                .tag("board")
                                .pathParameters(
                                        parameterWithName("id").description("게시글 ID")
                                )
                                .responseFields(
                                        fieldWithPath("board").description("수정할 게시글 정보(JSON)")
                                )
                                .responseFields(
                                        fieldWithPath("id").description("게시글 ID"),
                                        fieldWithPath("attachFileGroupId").description("첨부파일 그룹 ID").optional(),
                                        fieldWithPath("content").description("수정된 게시글 내용"),
                                        fieldWithPath("title").description("수정된 제목"),
                                        fieldWithPath("createMemberId").description("작성자").optional(),
                                        fieldWithPath("updateMemberId").description("수정자 ID"),
                                        fieldWithPath("createdAt").description("작성일").optional(),
                                        fieldWithPath("updatedAt").description("수정일").optional(),
                                        fieldWithPath("viewCount").description("조회수").optional(),
                                        fieldWithPath("isPin").description("상단 고정 여부")
                                )
                                .build()
                        )
                ));
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteBoard() throws Exception {
        Mockito.doNothing().when(boardFacade).deleteBoard(1L);

        mockMvc.perform(delete("/api/boards/{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("board/delete",
                        resource(
                                ResourceSnippetParameters.builder()
                                        .tag("board")
                                        .build()
                        )
                ));
    }

}
