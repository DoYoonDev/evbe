package com.evolve.evolvebe.board.interfaces;

import com.evolve.evolvebe.board.domain.BoardCommand;
import com.evolve.evolvebe.board.domain.BoardInfo;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface BoardDtoMapper {

    // 게시글 전체 조회
    default BoardResponse.BoardListResponse mapToBoardList(List<BoardInfo.Summary> boards) {
        return BoardResponse.BoardListResponse.builder()
                .boardInfos(boards)
                .build();
    }

    // 게시글 상세 조회
    default BoardResponse.BoardDetailResponse toDetailResponse(BoardInfo.Detail board) {
        return BoardResponse.BoardDetailResponse.builder()
                .id(board.getId())
                .attachFileGroupId(board.getAttachFileGroupId())
                .title(board.getTitle())
                .content(board.getContent())
                .createMemberId(board.getCreateMemberId())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .viewCount(board.getViewCount())
                .isPin(board.getIsPin())
                .attachFiles(
                        board.getAttachFiles() == null ? List.of() :
                        board.getAttachFiles().stream()
                                .map(file -> BoardResponse.BoardDetailResponse.AttachFileResponse.builder()
                                        .id(file.getId())
                                        .name(file.getName())
                                        .saveName(file.getSaveName())
                                        .path(file.getPath())
                                        .build()
                                ).toList()
                )
                .build();
    }

    // 게시글 등록
    default BoardCommand.Create toCreateCommand(BoardRequest.Create request, List<MultipartFile> files){
        if ( request == null && files == null ) {
            return null;
        }

        BoardCommand.Create.CreateBuilder create = BoardCommand.Create.builder();

        if ( request != null ) {
            create.content( request.getContent() );
            create.title( request.getTitle() );
            create.createMemberId( request.getCreateMemberId() );
            create.isPin( request.getIsPin() );
        }

        return create.build();
    }

    // 수정 요청 → 수정 커맨드
    default BoardCommand.Update toUpdateCommand(Long boardId, BoardRequest.Update request, List<MultipartFile> files) {
        if ( boardId == null && request == null && files == null ) {
            return null;
        }

        BoardCommand.Update.UpdateBuilder update = BoardCommand.Update.builder();
        update.id(boardId);

        if ( request != null ) {
            update.content( request.getContent() );
            update.title( request.getTitle() );
            update.updateMemberId( request.getUpdateMemberId() );
            update.isPin( request.getIsPin() );
        }

        return update.build();
    };

    // 수정 결과 → 응답 변환
    default BoardResponse.BoardUpdateResponse toUpdateResponse(BoardInfo.Detail info) {
        if ( info == null ) {
            return null;
        }

        BoardResponse.BoardUpdateResponse.BoardUpdateResponseBuilder boardUpdateResponse = BoardResponse.BoardUpdateResponse.builder();

        boardUpdateResponse.id( info.getId() );
        boardUpdateResponse.attachFileGroupId( info.getAttachFileGroupId() );
        boardUpdateResponse.content( info.getContent() );
        boardUpdateResponse.title( info.getTitle() );
        boardUpdateResponse.createMemberId( info.getCreateMemberId() );
        boardUpdateResponse.createdAt( info.getCreatedAt() );
        boardUpdateResponse.updatedAt( info.getUpdatedAt() );
        boardUpdateResponse.viewCount( info.getViewCount() );
        boardUpdateResponse.isPin( info.getIsPin() );

        return boardUpdateResponse.build();
    };

    BoardResponse.BoardPageResponse mapToBoardPage(Page<BoardInfo.Summary> pageData);
}
