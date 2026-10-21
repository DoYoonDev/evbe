package com.evolve.evolvebe.board.domain;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface BoardInfoMapper {

    BoardInfo.Detail mapToDetail(Board board);

    BoardInfo.Summary mapToSummary(Board board);

    List<BoardInfo.Summary> mapToSummaryList(List<Board> boards);

    default Page<BoardInfo.Summary> mapToSummaryPage(Page<Board> page) {
        return page.map(this::mapToSummary);
    }
}
