package com.evolve.evolvebe.code.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "code")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Code extends AbstractEntity {

	@Id
	private String id;

	@ManyToOne
	@JoinColumn(name = "code_group_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CodeGroup codeGroup;

	@Column(nullable = false)
	private String messageId;

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
	public Code(
			String id,
			CodeGroup codeGroup,
			String messageId,
			String name,
			String description,
			Integer sortOrder,
			Boolean isActive
	) {
		this.id = id;
		this.codeGroup = codeGroup;
		this.messageId = messageId;
		this.name = name;
		this.description = description;
		this.sortOrder = sortOrder;
		this.isActive = isActive;
	}

	public void update(
			String name,
			String description,
			Integer sortOrder,
			Boolean isActive
	) {
		this.name = name;
		this.description = description;
		this.sortOrder = sortOrder;
		this.isActive = isActive;
	}

}
