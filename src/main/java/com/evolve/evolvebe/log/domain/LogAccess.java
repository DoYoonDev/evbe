package com.evolve.evolvebe.log.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Entity
@Table(name = "log_access")
@Getter
@NoArgsConstructor

@EntityListeners(AuditingEntityListener.class)
public class LogAccess {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "member_id")
//	private Member member;
	private String memberId;

	private String requestUrl;

	private String requestMethod;

	private String requestPayload;

	private String requestIp;

	private Integer responseStatus;

	private String responseBody;

	private String userAgent;

	@CreationTimestamp
	private ZonedDateTime createdAt;

	@Builder(access = lombok.AccessLevel.PROTECTED)
	public LogAccess(
//		Member member,
		String memberId,
		String requestUrl,
		String requestMethod,
		String requestPayload,
		String requestIp,
		Integer responseStatus,
		String responseBody,
		String userAgent
	) {
//		this.member = member;
		this.memberId = memberId;
		this.requestUrl = requestUrl;
		this.requestMethod = requestMethod;
		this.requestPayload = requestPayload;
		this.requestIp = requestIp;
		this.responseStatus = responseStatus;
		this.responseBody = responseBody;
		this.userAgent = userAgent;
	}

}
