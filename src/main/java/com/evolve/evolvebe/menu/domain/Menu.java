package com.evolve.evolvebe.menu.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "menu")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Menu extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer parentMenuId;

	private String messageId;

	private String url;

	@Column(nullable = false)
	private String name;

	private Boolean isActive;

	private Boolean isDelete;

	private Integer sortOrder;

	private String description;

	private Integer level;

	@CreatedBy
	@Column(name = "create_member_id", updatable = false)
	private String createMemberId;

	@LastModifiedBy
	@Column(name = "update_member_id")
	private String updateMemberId;

	@Builder(access = lombok.AccessLevel.PROTECTED)
	public Menu(
		Integer id,
		Integer parentMenuId,
		String messageId,
		String url,
		String name,
		Boolean isActive,
		Boolean isDelete,
		Integer sortOrder,
		String description,
		Integer level
	) {
		this.id = id;
		this.parentMenuId = parentMenuId;
		this.messageId = messageId;
		this.url = url;
		this.name = name;
		this.isActive = isActive;
		this.isDelete = isDelete;
		this.sortOrder = sortOrder;
		this.description = description;
		this.level = level;
	}

	public void update(
		String messageId,
		String url,
		String name,
		Boolean isActive,
		String description
	) {
		this.messageId = messageId;
		this.url = url;
		this.name = name;
		this.isActive = isActive;
		this.description = description;
	}

	public void update(
		Integer parentMenuId,
		Integer sortOrder,
		Integer level
	) {
		this.parentMenuId = parentMenuId;
		this.sortOrder = sortOrder;
		this.level = level;
	}

	public void update(
		Boolean isDelete
	) {
		this.isDelete = isDelete;
	}
}
