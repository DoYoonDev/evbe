package com.evolve.evolvebe.role.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "role_api")
@NoArgsConstructor
public class RoleApi {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	private String url;
	private String method;

	@Builder(access = lombok.AccessLevel.PROTECTED)
	public RoleApi(
		Role role,
		String url,
		String method
	) {
		this.role = role;
		this.url = url;
		this.method = method;
	}

	public static RoleApi create(Role role, String url, String method) {
		return RoleApi.builder()
			.role(role)
			.url(url)
			.method(method)
			.build();
	}

}
