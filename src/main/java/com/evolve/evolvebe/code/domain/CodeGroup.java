package com.evolve.evolvebe.code.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "code_group")
@NoArgsConstructor
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class CodeGroup extends AbstractEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    private Integer sortOrder;

    private Boolean isActive;

    @CreatedBy
    @Column(name = "create_member_id", updatable = false)
    private String createMemberId;

    @LastModifiedBy
    @Column(name = "update_member_id")
    private String updateMemberId;

    @Builder(access = lombok.AccessLevel.PROTECTED)
    public CodeGroup(
            String id,
            String name,
            String description,
            Integer sortOrder,
            Boolean isActive
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
    }

    public void update(
        String name,
        String description,
        Boolean isActive
    ) {
        this.name = name;
        this.description = description;
        this.isActive = isActive;
    }

}
