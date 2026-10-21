package com.evolve.evolvebe.log;

import com.evolve.evolvebe.log.application.LogFacade;

import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogInfo;
import com.evolve.evolvebe.log.domain.LogRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class LogIntegrationTest {

	@Autowired
	private LogFacade logFacade;

	@Autowired
	private LogRepository logRepository;

	@Test
	void getLogAccesss() {
		// given
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		LogCommand.GetLogAccessList command = LogCommand.GetLogAccessList.builder()
			.memberId("userId")
			.accessedAtStart("")
			.accessedAtEnd("")
			.build();

		// when
		Page<LogInfo.LogAccess> result = logFacade.getLogAccess(command, pageable);

		// then
		assertEquals(2, result.getContent().size());
	}
}
