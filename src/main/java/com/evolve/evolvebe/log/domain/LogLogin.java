package com.evolve.evolvebe.log.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

@Entity
@Getter
@Table(name = "log_login")
@NoArgsConstructor

@EntityListeners(AuditingEntityListener.class)
public class LogLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String accessIp;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private boolean success;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Builder(access = AccessLevel.PROTECTED)
    public LogLogin(
        String memberId,
        String accessIp,
        String userAgent,
        boolean success
    ) {
        this.memberId = memberId;
        this.accessIp = accessIp;
        this.userAgent = userAgent;
        this.success = success;
    }

}
