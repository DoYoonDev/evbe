package com.evolve.evolvebe.log.infra;

import com.evolve.evolvebe.log.domain.LogLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.ZonedDateTime;

public interface LogLoginJpaRepository extends JpaRepository<LogLogin, Integer>, JpaSpecificationExecutor<LogLogin> {

    Page<LogLogin> findByMemberIdContainingAndCreatedAtBetween(String memberId, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

    Page<LogLogin> findByMemberIdContaining(String memberId, Pageable pageable);
}
