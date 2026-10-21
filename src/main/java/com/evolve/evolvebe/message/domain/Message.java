package com.evolve.evolvebe.message.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "message")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Message extends AbstractEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String koText;

    @Column(nullable = false)
    private String enText;

    @Column(nullable = false)
    private Boolean isActive;

    @CreatedBy
    @Column(name = "create_member_id", nullable = false, updatable = false)
    private String createMemberId;

    @LastModifiedBy
    @Column(name = "update_member_id", nullable = false)
    private String updateMemberId;

    @Builder(access = lombok.AccessLevel.PROTECTED)
    public Message(
        String id,
        Boolean isActive,
        String koText,
        String enText
    ) {
        this.id = id;
        this.isActive = isActive;
        this.koText = koText;
        this.enText = enText;
    }

    public void update(
        String koText,
        String enText,
        Boolean isActive
    ) {
        this.koText = koText;
        this.enText = enText;
        this.isActive = isActive;
    }

}
