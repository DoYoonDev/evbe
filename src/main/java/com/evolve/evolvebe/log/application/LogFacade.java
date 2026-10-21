package com.evolve.evolvebe.log.application;

import com.evolve.evolvebe.log.domain.LogInfo;
import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogFacade {

	private final LogService logService;

	@Transactional(readOnly = true)
	public Page<LogInfo.LogAccess> getLogAccess(LogCommand.GetLogAccessList command, Pageable pageable) {
		return logService.getLogAccess(command, pageable);
	}

	@Transactional
	public void createLogAccess(LogCommand.CreateLogAccess createLogAccess) {
		logService.saveLogAccess(createLogAccess);
	}

	@Transactional
	public void createLogLogin(LogCommand.CreateLogLogin createLogLogin) {
		logService.saveLogLogin(createLogLogin);
	}

	@Transactional(readOnly = true)
	public Page<LogInfo.LogLogin> getLogLogin(LogCommand.GetLogLoginList command, Pageable pageable) {
		return logService.getLogLogin(command, pageable);
	}
}
