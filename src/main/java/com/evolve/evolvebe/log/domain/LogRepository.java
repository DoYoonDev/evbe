package com.evolve.evolvebe.log.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LogRepository {

	Page<LogAccess> getLogAccess(LogCommand.GetLogAccessList command, Pageable pageable);

	void saveLogAccess(LogAccess logAccess);

	void saveLogLogin(LogLogin logLogin);

	Page<LogLogin> getLogLogin(LogCommand.GetLogLoginList command, Pageable pageable);
}