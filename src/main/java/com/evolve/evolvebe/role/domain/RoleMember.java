package com.evolve.evolvebe.role.domain;

import com.evolve.evolvebe.member.domain.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "role_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder(access = lombok.AccessLevel.PROTECTED)
	public RoleMember(
		Role role,
		Member member
	) {
		this.role = role;
		this.member = member;
	}

	public static RoleMember create(Role role, Member member) {
		return RoleMember.builder()
			.role(role)
			.member(member)
			.build();
	}

}
