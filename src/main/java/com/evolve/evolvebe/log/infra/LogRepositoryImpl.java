package com.evolve.evolvebe.log.infra;

import com.evolve.evolvebe.log.domain.LogAccess;
import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogLogin;
import com.evolve.evolvebe.log.domain.LogRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
@RequiredArgsConstructor
public class LogRepositoryImpl implements LogRepository {

	private final LogAccessJpaRepository logAccessJpaRepository;
	private final LogLoginJpaRepository logLoginJpaRepository;

	@Override
	public Page<LogAccess> getLogAccess(LogCommand.GetLogAccessList command, Pageable pageable) {
		ZonedDateTime start = (command.getAccessedAtStart() == null || command.getAccessedAtStart().isEmpty())
				? null
				: ZonedDateTime.parse(command.getAccessedAtStart());
		ZonedDateTime end = (command.getAccessedAtEnd() == null || command.getAccessedAtEnd().isEmpty())
				? null
				: ZonedDateTime.parse(command.getAccessedAtEnd()).plusDays(1).minusNanos(1);

		if (start != null && end != null) {
			return logAccessJpaRepository.findByMemberIdContainingAndCreatedAtBetween(
					command.getMemberId(), start, end, pageable
			);
		} else {
			return logAccessJpaRepository.findByMemberIdContaining(
					command.getMemberId(), pageable
			);
		}
	}

	@Override
	public void saveLogAccess(LogAccess logAccess) {
		logAccessJpaRepository.save(logAccess);
	}

	@Override
	public void saveLogLogin(LogLogin logLogin) {
		logLoginJpaRepository.save(logLogin);
	}

	@Override
	public Page<LogLogin> getLogLogin(LogCommand.GetLogLoginList command, Pageable pageable) {
		ZonedDateTime start = (command.getAccessedAtStart() == null || command.getAccessedAtStart().isEmpty())
				? null
				: ZonedDateTime.parse(command.getAccessedAtStart());
		ZonedDateTime end = (command.getAccessedAtEnd() == null || command.getAccessedAtEnd().isEmpty())
				? null
				: ZonedDateTime.parse(command.getAccessedAtEnd()).plusDays(1).minusNanos(1);

		if (start != null && end != null) {
			return logLoginJpaRepository.findByMemberIdContainingAndCreatedAtBetween(
					command.getMemberId(), start, end, pageable
			);
		} else {
			return logLoginJpaRepository.findByMemberIdContaining(
					command.getMemberId(), pageable
			);
		}
	}
}
