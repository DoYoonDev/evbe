package com.evolve.evolvebe.board.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.function.BiFunction;

public enum BoardSearchColumn {
    ATTACH_FILE_GROUP_ID("attachFileGroupId", (repo, param) -> repo.findByAttachFileGroupIdContaining(param.keyword(), param.pageable())),
    TITLE("title", (repo, param) -> repo.findByTitleContaining(param.keyword(), param.pageable())),
    CREATE_MEMBER_ID("title", (repo, param) -> repo.findByCreateMemberIdContaining(param.keyword(), param.pageable()));

    private final String value;
    private final BiFunction<BoardRepository, SearchParams, Page<Board>> searchFunction;

    BoardSearchColumn(String value, BiFunction<BoardRepository, SearchParams, Page<Board>> searchFunction) {
        this.value = value;
        this.searchFunction = searchFunction;
    }

    public static BoardSearchColumn from(String value) {
        for (BoardSearchColumn c : values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 검색 컬럼: " + value);
    }

    public Page<Board> search(BoardRepository repo, String keyword, Pageable pageable) {
        return searchFunction.apply(repo, new SearchParams(keyword, pageable));
    }

    private record SearchParams(String keyword, Pageable pageable) {}

}
