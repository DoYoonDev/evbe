package com.evolve.evolvebe.role.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Role extends AbstractEntity {

	@Id
	private String id;

	private String name;
	private String description;
	private Boolean isActive;

	@Column(name = "\"level\"", nullable = false)
//    @Column(nullable = false)
    private Integer level = 0;

	@CreatedBy
	@Column(name = "create_member_id", updatable = false)
	private String createMemberId;

	@LastModifiedBy
	@Column(name = "update_member_id")
	private String updateMemberId;

	@Builder(access = lombok.AccessLevel.PROTECTED)
	public Role(
		String id,
		String name,
		String description,
		Boolean isActive,
        Integer level
	) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.isActive = isActive;
        this.level = level;
	}

	public static Role create(String id, String name, String description, Boolean isActive, Integer level) {
        return Role.builder()
                .id(id)
                .name(name)
                .description(description)
                .isActive(isActive)
                .level(level != null ? level : 0)
                .build();
	}

	public void update(String name, String description, Boolean isActive, Integer level) {
		this.name = name;
		this.description = description;
		this.isActive = isActive;
        this.level = level;
	}

}
