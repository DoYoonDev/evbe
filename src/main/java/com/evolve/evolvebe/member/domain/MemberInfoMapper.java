package com.evolve.evolvebe.member.domain;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface MemberInfoMapper {
	// 내부
	MemberInfo.Main mapToMain(Member member);

	// 외부
	MemberInfo.Profile mapToProfile(Member member);

	MemberInfo.Summary mapToSummary(Member member);

	List<MemberInfo.Summary> mapToSummaryList(List<Member> members);

	default Page<MemberInfo.Summary> mapToSummaryPage(Page<Member> page) {
		return page.map(this::mapToSummary);
	}
}
