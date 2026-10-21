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
@Table(name = "member_terms_agreement")
@NoArgsConstructor

@EntityListeners(AuditingEntityListener.class)
public class TermsMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String termsId;

    @Column(nullable = false)
    private boolean agreed;

    @CreationTimestamp
    private ZonedDateTime agreedAt;

    @Builder(access = AccessLevel.PROTECTED)
    public TermsMember(
            String memberId,
            String termsId,
            boolean agreed
    ) {
        this.memberId = memberId;
        this.termsId = termsId;
        this.agreed = agreed;
    }
}
