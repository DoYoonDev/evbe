package com.evolve.evolvebe.log.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

	@Mock
	private LogRepository logRepository;

	@Mock
	private LogInfoMapper logInfoMapper;

	@InjectMocks
	private LogService logService;

	@Test
	@DisplayName("접근 로그 목록 조회")
	void getLogAccess() {
		// given
		List<LogAccess> logAccesses = List.of(mock(LogAccess.class), mock(LogAccess.class));
		Pageable pageable = PageRequest.of(0, 10);
		LogCommand.GetLogAccessList command = mock(LogCommand.GetLogAccessList.class);
		Page<LogAccess> logAccessPage = new PageImpl<>(logAccesses, pageable, logAccesses.size());
		Page<LogInfo.LogAccess> logAccessInfos = new PageImpl<>(
			List.of(mock(LogInfo.LogAccess.class), mock(LogInfo.LogAccess.class)));

		when(logRepository.getLogAccess(command, pageable)).thenReturn(logAccessPage);
		when(logInfoMapper.from(logAccessPage)).thenReturn(logAccessInfos);

		// when
		Page<LogInfo.LogAccess> result = logService.getLogAccess(command, pageable);

		// then
		assertEquals(2, result.getContent().size());
		verify(logRepository).getLogAccess(command, pageable);
		verify(logInfoMapper).from(logAccessPage);
	}

	@Test
	@DisplayName("로그인 로그 목록 조회")
	void getLogLogin() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		List<LogLogin> logLogins = List.of(mock(LogLogin.class), mock(LogLogin.class));
		LogCommand.GetLogLoginList command = mock(LogCommand.GetLogLoginList.class);
		Page<LogLogin> logLoginPage = new PageImpl<>(logLogins, pageable, logLogins.size());
		Page<LogInfo.LogLogin> logLoginInfos = new PageImpl<>(
				List.of(mock(LogInfo.LogLogin.class), mock(LogInfo.LogLogin.class))
		);
		when(logRepository.getLogLogin(command, pageable)).thenReturn(logLoginPage);
		when(logInfoMapper.mapToLogLoginPage(logLoginPage)).thenReturn(logLoginInfos);

		// when
		Page<LogInfo.LogLogin> result = logService.getLogLogin(command, pageable);

		// then
		assertEquals(2, result.getContent().size());
		verify(logRepository).getLogLogin(command, pageable);
		verify(logInfoMapper).mapToLogLoginPage(logLoginPage);
	}

	@Test
	@DisplayName("접근 로그 저장")
	void saveLogAccess() {
		// given
		LogCommand.CreateLogAccess logAccess = mock(LogCommand.CreateLogAccess.class);
		LogAccess logAccessEntity = mock(LogAccess.class);
		when(logAccess.toEntity()).thenReturn(logAccessEntity);

		// when
		logService.saveLogAccess(logAccess);

		// then
		verify(logRepository).saveLogAccess(logAccessEntity);
	}

}