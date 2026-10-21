package com.evolve.evolvebe.log.domain;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

	private final LogRepository logRepository;
	private final LogInfoMapper logInfoMapper;

	@Transactional(readOnly = true)
	public Page<LogInfo.LogAccess> getLogAccess(LogCommand.GetLogAccessList command, Pageable pageable) {
		Page<LogAccess> logAccesses = logRepository.getLogAccess(command, pageable);
		return logInfoMapper.from(logAccesses);
	}

	public void saveLogAccess(LogCommand.CreateLogAccess logAccess) {
		logRepository.saveLogAccess(logAccess.toEntity());
	}

	@Transactional
	public void saveLogLogin(LogCommand.CreateLogLogin createLogLogin) {
		logRepository.saveLogLogin(createLogLogin.toEntity());
	}

	@Transactional(readOnly = true)
	public Page<LogInfo.LogLogin> getLogLogin(LogCommand.GetLogLoginList command, Pageable pageable) {
		Page<LogLogin> logLogins = logRepository.getLogLogin(command, pageable);
		return logInfoMapper.mapToLogLoginPage(logLogins);
	}

	private boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}
}
