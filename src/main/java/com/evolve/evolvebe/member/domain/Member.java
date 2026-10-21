package com.evolve.evolvebe.member.domain;

import com.evolve.evolvebe.global.entity.AbstractEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "member")
@NoArgsConstructor
public class Member extends AbstractEntity {

	@Id
	private String id;

	@Column(nullable = false)
	private String koName;

	private String enName;

	@Column(nullable = false)
	private String email;

	private String phoneNumber;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private Boolean isActive = true;

	@Column(nullable = false)
	private Boolean mustChangePassword = Boolean.FALSE;

	@Builder(access = AccessLevel.PROTECTED)
	public Member(
		String id,
		String koName,
		String enName,
		String email,
		String phoneNumber,
		String password,
		Boolean isActive,
		Boolean mustChangePassword
	) {
		this.id = id;
		this.koName = koName;
		this.enName = enName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.isActive = isActive != null ? isActive : Boolean.TRUE;
		this.mustChangePassword = mustChangePassword != null ? mustChangePassword : Boolean.FALSE;
	}

	// 사용자 생성
	public static Member create(
		String id,
		String koName,
		String enName,
		String email,
		String phoneNumber,
		String password,
		Boolean isActive,
		Boolean mustChangePassword
	) {
		return Member.builder()
				.id(id)
				.koName(koName)
				.enName(enName)
				.email(email)
				.phoneNumber(phoneNumber)
				.password(password)
				.isActive(isActive != null ? isActive : Boolean.TRUE)
				.mustChangePassword(mustChangePassword != null ? mustChangePassword : Boolean.FALSE)
				.build();
	}

	// 정보 수정
	public void updateInfo(
		String koName,
		String enName,
		String email,
		String phoneNumber,
		Boolean isActive
	) {
		this.koName = koName;
		this.enName = enName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.isActive = isActive != null ? isActive : true;
	}

	// 패스워드 변경
	public void updatePw(
			String password
	) {
		this.password = password;
	}

	// 패스워드 초기화
	public void resetPassword(
			String encodedPassword
	) {
		this.password = encodedPassword;
		this.mustChangePassword = Boolean.TRUE;
	}
}
