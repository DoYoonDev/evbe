package com.evolve.evolvebe.log.application;

import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogInfo;
import com.evolve.evolvebe.log.domain.LogService;

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
import org.springframework.security.core.parameters.P;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogFacadeTest {

	@Mock
	private LogService logService;

	@InjectMocks
	private LogFacade logFacade;

	@Test
	@DisplayName("접근 로그 목록 조회")
	void getLogAccess() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		LogCommand.GetLogAccessList command = mock(LogCommand.GetLogAccessList.class);
		Page<LogInfo.LogAccess> logAccessInfos = new PageImpl<>(
			List.of(mock(LogInfo.LogAccess.class))
		);
		when(logService.getLogAccess(command, pageable)).thenReturn(logAccessInfos);

		// when
		Page<LogInfo.LogAccess> result = logFacade.getLogAccess(command, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(logAccessInfos.getSize());
		verify(logService).getLogAccess(command, pageable);
	}

	@Test
	@DisplayName("로그인 로그 목록 조회")
	void getLogLogin() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		LogCommand.GetLogLoginList command = mock(LogCommand.GetLogLoginList.class);
		Page<LogInfo.LogLogin> logLoginInfos = new PageImpl<>(
				List.of(mock(LogInfo.LogLogin.class))
		);
		when(logService.getLogLogin(command, pageable)).thenReturn(logLoginInfos);

		// when
		Page<LogInfo.LogLogin> result = logFacade.getLogLogin(command, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(logLoginInfos.getSize());
		verify(logService).getLogLogin(command, pageable);
	}

	@Test
	@DisplayName("접근 로그 저장")
	void createLogAccess() {
		// given
		LogCommand.CreateLogAccess logAccess = mock(LogCommand.CreateLogAccess.class);

		// when
		logFacade.createLogAccess(logAccess);

		// then
		verify(logService).saveLogAccess(logAccess);
	}

}