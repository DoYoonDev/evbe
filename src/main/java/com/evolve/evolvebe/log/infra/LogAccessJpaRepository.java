package com.evolve.evolvebe.log.infra;

import com.evolve.evolvebe.log.domain.LogAccess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;

public interface LogAccessJpaRepository extends JpaRepository<LogAccess, Integer> {

	Page<LogAccess> findByMemberIdContainingAndRequestUrlContainingAndRequestMethodContainingAndRequestIpContaining(
		Pageable pageable, String memberId, String requestUrl, String requestMethod, String requestIp
	);

    Page<LogAccess> findByMemberIdContainingAndCreatedAtBetween(String memberId, ZonedDateTime start, ZonedDateTime end, Pageable pageable);

	Page<LogAccess> findByMemberIdContaining(String memberId, Pageable pageable);
}
