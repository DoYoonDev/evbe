package com.evolve.evolvebe.board.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "attach_file")
@NoArgsConstructor
public class AttachFile extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String saveName;

    @Column
    private String sortOrder;

    @Column
    private String size;

    @Column
    private String extension;

    @Column
    private String path;

    @Column
    private String createMemberId;

    @Column
    private String updateMemberId;

    // ← groupId 제거, Board 참조만 남김
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id") // DB 컬럼 이름과 일치
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    @Builder(access = AccessLevel.PROTECTED)
    public AttachFile(
            String name,
            String saveName,
            String sortOrder,
            String size,
            String extension,
            String path,
            String createMemberId,
            String updateMemberId
    ) {
        this.name = name;
        this.saveName = saveName;
        this.sortOrder = sortOrder;
        this.size = size;
        this.extension = extension;
        this.path = path;
        this.createMemberId = createMemberId;
        this.updateMemberId = updateMemberId;
    }
}

