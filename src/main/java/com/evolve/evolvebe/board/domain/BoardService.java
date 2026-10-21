package com.evolve.evolvebe.board.domain;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static jodd.io.FileNameUtil.getExtension;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardInfoMapper boardInfoMapper;
    private final AttachFileRepository attachFileRepository;

    // 전체 목록 조회
    @Transactional(readOnly = true)
    public List<BoardInfo.Summary> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boardInfoMapper.mapToSummaryList(boards);
    }

    // 전체 목록 조회(page)
    @Transactional(readOnly = true)
    public Page<BoardInfo.Summary> getAllBoardsPaged(String column, String keyword, Pageable pageable) {
        Page<Board> boards;

        if (isBlank(keyword)) {
            boards = boardRepository.findAllPaged(pageable);
        } else {
            BoardSearchColumn col = BoardSearchColumn.from(column); // enum 변환
            boards = col.search(boardRepository, keyword, pageable);
        }

        return boardInfoMapper.mapToSummaryPage(boards);
    }

    private boolean isBlank(String str) { return str == null || str.trim().isEmpty(); }

    // 게시글 상세 조회
    @Transactional
    public BoardInfo.Detail getBoardDetail(long boardId, HttpSession session) {

        // 1. 세션 기반 조회수 증가
        Set<Long> viewed = (Set<Long>) session.getAttribute("VIEWED_BOARD_IDS");
        if (viewed == null) {
            viewed = new HashSet<>();
        }

        if (!viewed.contains(boardId)) {
            boardRepository.increaseViewCount(boardId);
            viewed.add(boardId);
            session.setAttribute("VIEWED_BOARD_IDS", viewed);
        }

        // 2. 게시글 + 첨부파일 fetch
        Board board = boardRepository.findWithAttachFilesById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        return boardInfoMapper.mapToDetail(board);
    }

    // 게시글 등록
    @Transactional
    public BoardInfo.Detail createBoard(BoardCommand.Create command, List<MultipartFile> files) throws IOException {

        // 1. Board + AttachFile 한 번에 저장
        Board board = boardRepository.saveBoard(command.toEntity());

        // 2. MultipartFile → AttachFileCommand.Create 변환
        if (files != null && !files.isEmpty()) {
            board.setAttachFileGroupId(board.getId());
            String baseUploadDir = "C:/upload/boards/";
            String uploadDir = baseUploadDir + board.getId(); // 실제 저장 경로
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : files) {
                // 고유 파일명 생성
                String saveName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(dir, saveName);

                String downloadPath = "files/" + board.getId() + "/" + saveName;

                // 서버에 실제 저장
                file.transferTo(dest);

                // AttachFile 엔티티 생성 및 양방향 세팅
                AttachFile attachFile = AttachFile.builder()
                        .name(file.getOriginalFilename())   // 사용자에게 보여줄 이름
                        .saveName(saveName)                 // 서버 저장 파일명
                        .path(downloadPath)                 // 다운로드할 때 쓸 URL
                        .size(String.valueOf(file.getSize()))
                        .extension(getExtension(file.getOriginalFilename()))
                        .build();

                board.addAttachFile(attachFile);
            }
        }

        // 4. 게시글 + 첨부파일 저장 (cascade = ALL 적용)
        boardRepository.saveBoard(board);

        return boardInfoMapper.mapToDetail(board);
    }

    // 첨부파일 가져오기
    @Transactional
    public UrlResource getFileResource(Long boardId, String saveName) throws MalformedURLException {
        AttachFile attachFile = attachFileRepository
                .findByBoardIdAndSaveName(boardId, saveName)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUNT_ATTACHFILE));

        Path file = Paths.get("C:/upload/boards/" + boardId + "/" + saveName); // 실제 파일 경로
        System.out.println("실제 파일 경로: " + file);

        UrlResource resource = new UrlResource(file.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new CustomException(ErrorCode.NOT_FOUNT_ATTACHFILE);
        }

        return resource;
    }

    // 게시글 수정
    @Transactional
    public BoardInfo.Detail updateBoard(BoardCommand.Update command, List<MultipartFile> attachFiles) throws IOException {
        Board board = boardRepository.getBoard(command.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        board.clearAttachFiles();

        if (board.getAttachFiles() == null || board.getAttachFiles().isEmpty()) {
            board.setAttachFileGroupId(null);
        }

        // 본문/제목 업데이트
        board.updateInfo(
                command.getTitle(),
                command.getContent(),
                command.getUpdateMemberId(),
                command.getIsPin()
        );

        if ( attachFiles != null && !attachFiles.isEmpty() ) {

            String baseUploadDir = "C:/upload/boards/";
            String uploadDir = baseUploadDir + board.getId(); // 실제 저장 경로
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            for (MultipartFile file : attachFiles) {

                String saveName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File dest = new File(dir, saveName);

                String downloadPath = "files/" + board.getId() + "/" + saveName;

                // 서버에 실제 저장
                file.transferTo(dest);

                AttachFile attachFile = AttachFile.builder()
                        .name(file.getOriginalFilename())
                        .saveName(saveName)
                        .path(downloadPath)
                        .extension(getExtension(file.getOriginalFilename()))
                        .size(String.valueOf(file.getSize()))
                        .updateMemberId(command.getUpdateMemberId())
                        .build();

                board.addAttachFile(attachFile);
                board.setAttachFileGroupId(command.getId());
            }
        }

        boardRepository.saveBoard(board);

        return boardInfoMapper.mapToDetail(board);
    }

    // 게시글 삭제
    public void deleteBoard(Long id) {
        Board board = boardRepository.getBoard(id)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

//        board.clearAttachFiles();

        boardRepository.delete(board);
    }
}
