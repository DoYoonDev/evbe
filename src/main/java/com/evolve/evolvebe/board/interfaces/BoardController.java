package com.evolve.evolvebe.board.interfaces;

import com.evolve.evolvebe.board.application.BoardFacade;
import com.evolve.evolvebe.board.domain.BoardCommand;
import com.evolve.evolvebe.board.domain.BoardInfo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardFacade boardFacade;
    private final BoardDtoMapper boardDtoMapper;

    // 전체 게시글 조회
    @GetMapping
    public ResponseEntity<BoardResponse.BoardListResponse> getAllBoards() {
        List<BoardInfo.Summary> result = boardFacade.getAllBoards();
        BoardResponse.BoardListResponse response = boardDtoMapper.mapToBoardList(result);
        return ResponseEntity.ok(response);
    }

    // 전체 게시글 조회 (페이징)
    @GetMapping("/page")
    public ResponseEntity<BoardResponse.BoardPageResponse> getBoardsPaged(
            @RequestParam(required = false) String column,
            @RequestParam(required = false) String keyword,
            @PageableDefault(sort = "createdAt", direction =Sort.Direction.DESC, size = 10)
            Pageable pageable
    ) {
        Sort sort = Sort.by(Sort.Order.desc("isPin"));

        if (pageable.getSort().isSorted()) {
            sort = sort.and(pageable.getSort());
        } else {
            sort = sort.and(Sort.by(Sort.Order.desc("createdAt")));
        }

        Pageable newPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<BoardInfo.Summary> result = boardFacade.getAllBoardsPaged(keyword, column, newPageable);
        BoardResponse.BoardPageResponse response = BoardResponse.BoardPageResponse.from(result);
        return ResponseEntity.ok(response);
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse.BoardDetailResponse> getBoardById(
            @PathVariable("id") long id,
            HttpSession session
    ) {
        BoardInfo.Detail result = boardFacade.getBoardDetail(id, session);
        BoardResponse.BoardDetailResponse response = boardDtoMapper.toDetailResponse(result);

        return ResponseEntity.ok(response);
    }

    // 첨부파일 다운로드
    @GetMapping("/files/{boardId}/{saveName}")
    public ResponseEntity<UrlResource> viewOrDownloadFile(
            @PathVariable Long boardId,
            @PathVariable String saveName
    ) throws IOException {

        UrlResource resource = boardFacade.getFileResource(boardId, saveName);

        // 안전하게 원본 파일명 추출
        String originalFileName = saveName.contains("_") ?
                saveName.substring(saveName.indexOf("_") + 1) : saveName;

        // 파일의 실제 MIME 타입 추출
        Path filePath = Paths.get("C:/upload/boards/" + boardId + "/" + saveName);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream"; // 알 수 없을 때
        }

        // 브라우저에서 열 수 있는 건 열리고, 못 여는 건 다운로드됨
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        // inline: 브라우저에서 열림, attachment: 다운로드
                        "inline; filename=\"" + originalFileName + "\"")
                .body(resource);
    }

    // 게시글 등록
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> createBoard(
            @RequestPart("board") BoardRequest.Create request,
            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles
    ) throws IOException {
        BoardCommand.Create command = boardDtoMapper.toCreateCommand(request, attachFiles);
        boardFacade.createBoard(command, attachFiles);
        return ResponseEntity.ok(Boolean.TRUE);
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse.BoardUpdateResponse> updateBoard(
            @PathVariable("id") Long boardId,
            @RequestPart("board") BoardRequest.Update request,
            @RequestPart(value = "attachFiles", required = false) List<MultipartFile> attachFiles
    ) throws IOException {

        BoardCommand.Update command = boardDtoMapper.toUpdateCommand(boardId, request, attachFiles);
        BoardInfo.Detail update = boardFacade.updateBoard(command, attachFiles);
        BoardResponse.BoardUpdateResponse response = boardDtoMapper.toUpdateResponse(update);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardFacade.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }

}
