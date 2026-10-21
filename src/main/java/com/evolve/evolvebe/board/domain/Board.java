package com.evolve.evolvebe.board.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "board")
@NoArgsConstructor
public class Board extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long attachFileGroupId;

    @Column
    private String content;

    @Column
    private String title;

    @Column
    private String createMemberId;

    @Column
    private String updateMemberId;

    @Column
    private int viewCount;

    @Column
    private Boolean isPin;

    // Board가 AttachFile을 소유
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttachFile> attachFiles = new ArrayList<>();

    @Builder(access = AccessLevel.PROTECTED)
    public Board(
            Long id,
            Long attachFileGroupId,
            String content,
            String title,
            String createMemberId,
            String updateMemberId,
            int viewCount,
            Boolean isPin
    ) {
        this.id = id;
        this.attachFileGroupId = attachFileGroupId;
        this.content = content;
        this.title = title;
        this.createMemberId = createMemberId;
        this.updateMemberId = updateMemberId;
        this.viewCount = viewCount;
        this.isPin = isPin;
    }

    public void addAttachFile(AttachFile attachFile) {
        attachFiles.add(attachFile);
        attachFile.setBoard(this);
    }

    public void clearAttachFiles() {
        for (AttachFile attachFile: new ArrayList<>(attachFiles)) {
            removeAttachFile(attachFile);
        }
    }

    public void removeAttachFile(AttachFile attachFile) {
        attachFiles.remove(attachFile);
        attachFile.setBoard(null);
    }

    public void updateInfo(
            String title,
            String content,
            String updateMemberId,
            Boolean isPin
    ) {
        this.attachFileGroupId = attachFileGroupId;
        this.title = title;
        this.content = content;
        this.updateMemberId = updateMemberId;
        this.isPin = isPin;
    }

}
