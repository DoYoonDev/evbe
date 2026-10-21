package com.evolve.evolvebe.log.domain;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface LogInfoMapper {

	LogInfo.LogAccess from(LogAccess logAccess);

	default Page<LogInfo.LogAccess> from(Page<LogAccess> logAccessList) {
		if (logAccessList == null) {
			return null;
		}

		List<LogInfo.LogAccess> list = new ArrayList<>(logAccessList.getSize());
		for (LogAccess logAccess : logAccessList) {
			list.add(from(logAccess));
		}

		return new PageImpl<>(list, logAccessList.getPageable(), logAccessList.getTotalElements());
	}

	LogInfo.LogLogin ofLogLoginMap(LogLogin logLogin);

	default Page<LogInfo.LogLogin> mapToLogLoginPage(Page<LogLogin> page) {
		return page.map(this::ofLogLoginMap);
	}
}
