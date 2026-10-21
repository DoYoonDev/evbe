package com.evolve.evolvebe.log.interfaces;

import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogInfo;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface LogDtoMapper {

	default LogResponse.LogAccessPage mapToLogAccessList(Page<LogInfo.LogAccess> logAccessInfos) {
		return LogResponse.LogAccessPage.builder()
				.logAccessInfos(logAccessInfos == null ? Page.empty() : logAccessInfos)
				.build();
	}

	LogCommand.GetLogAccessList logAccessFrom(LogRequest.GetLogAccessList request);

	LogCommand.GetLogLoginList logLoginFrom(LogRequest.GetLogLoginList request);

	default LogResponse.LogLoginPage mapToLogLoginList(Page<LogInfo.LogLogin> logLoginInfos) {
		return LogResponse.LogLoginPage.builder()
				.logLoginInfos(logLoginInfos == null ? Page.empty() : logLoginInfos)
				.build();
	}
}
