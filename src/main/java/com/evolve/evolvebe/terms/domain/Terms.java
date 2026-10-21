package com.evolve.evolvebe.terms.domain;

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
@Table(name = "terms")
@NoArgsConstructor

@EntityListeners(AuditingEntityListener.class)
public class Terms {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String langCd;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Builder(access = AccessLevel.PROTECTED)
    public Terms(
            String type,
            String version,
            String content,
            String langCd
    ) {
        this.type = type;
        this.version = version;
        this.content = content;
        this.langCd = langCd;
    }
}
