package com.evolve.evolvebe.board.infra;

import com.evolve.evolvebe.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardJpaRepository extends JpaRepository<Board, String> {

    Page<Board> findByAttachFileGroupIdContaining(String attachFileGroupId, Pageable pageable);
    Page<Board> findByTitleContaining(String title, Pageable pageable);
    Page<Board> findByCreateMemberIdContaining(String createMemberId, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE board SET view_count = view_count + 1 WHERE id = :id", nativeQuery = true)
    void increaseViewCount(@Param("id") Long id);

    @EntityGraph(attributePaths = "attachFiles")
    Optional<Board> findWithAttachFilesById(Long id);

}
