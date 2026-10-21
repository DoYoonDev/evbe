package com.evolve.evolvebe.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.EntityListeners;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 생성자/수정자만 담당하는 공통 엔티티.
 * 시간 필드(createdAt/updatedAt)는 기존 AbstractEntity에 둡니다.
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity extends AbstractEntity {

    @CreatedBy
    @Column(name = "create_member_id", updatable = false)
    protected String createMemberId;

    @LastModifiedBy
    @Column(name = "update_member_id")
    protected String updateMemberId;
}
