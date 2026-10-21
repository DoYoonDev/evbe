package com.evolve.evolvebe.log.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.evolve.evolvebe.log.application.LogFacade;
import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LogController {

	private final LogFacade logFacade;
	private final LogDtoMapper logDtoMapper;

	// 접근 로그 조회
	@GetMapping("/accesss")
	public ResponseEntity<LogResponse.LogAccessPage> getLogAccessList(
			@ModelAttribute LogRequest.GetLogAccessList request,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10)
			Pageable pageable
	) {
		LogCommand.GetLogAccessList command = logDtoMapper.logAccessFrom(request);
		Page<LogInfo.LogAccess> result = logFacade.getLogAccess(command, pageable);
		LogResponse.LogAccessPage response = logDtoMapper.mapToLogAccessList(result);
		return ResponseEntity.ok(response);
	}

	// 로그인 로그 조회
	@GetMapping("/logins")
	public ResponseEntity<LogResponse.LogLoginPage> getLogLoginList(
			@ModelAttribute LogRequest.GetLogLoginList request,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 10)
			Pageable pageable
	) {
		LogCommand.GetLogLoginList command = logDtoMapper.logLoginFrom(request);
		Page<LogInfo.LogLogin> result = logFacade.getLogLogin(command, pageable);
		LogResponse.LogLoginPage response = logDtoMapper.mapToLogLoginList(result);
		return ResponseEntity.ok(response);
	}
}
