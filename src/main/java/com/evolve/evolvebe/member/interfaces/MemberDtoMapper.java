package com.evolve.evolvebe.member.interfaces;

import java.util.List;

import com.evolve.evolvebe.member.domain.MemberCommand;

import org.mapstruct.Mapper;
import com.evolve.evolvebe.member.domain.MemberInfo;
import org.springframework.data.domain.Page;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface MemberDtoMapper {

	// 회원가입
	//MemberCommand.Create of(MemberRequest.Create request);

	default MemberResponse.MemberListResponse mapToMemberList(List<MemberInfo.Summary> members) {
		return MemberResponse.MemberListResponse.builder()
			.memberInfos(members)
			.build();
	}

	MemberCommand.Create toCommand(MemberRequest.Create request);

	default MemberResponse.MemberCreateResponse toCreateResponse(MemberInfo.Profile memberInfo) {
		return MemberResponse.MemberCreateResponse.builder()
				.id(memberInfo.getId())
				.message("회원 등록이 완료되었습니다.")
				.build();
	}

	default MemberResponse.MemberDetailResponse toDetailResponse(MemberInfo.Profile member) {
		return MemberResponse.MemberDetailResponse.builder()
				.id(member.getId())
				.koName(member.getKoName())
				.enName(member.getEnName())
				.email(member.getEmail())
				.phoneNumber(member.getPhoneNumber())
				.isActive(member.getIsActive())
				.createdAt(member.getCreatedAt())
				.updatedAt(member.getUpdatedAt())
				.build();
	}

	// 수정 요청 → 수정 커맨드
	default MemberCommand.Update toCommand(String id, MemberRequest.Update request) {
		return MemberCommand.Update.builder()
				.id(id)
				.koName(request.getKoName())
				.enName(request.getEnName())
				.email(request.getEmail())
				.phoneNumber(request.getPhoneNumber())
				.isActive(request.getIsActive())
				.build();
	}

	// 수정 결과 → 응답 변환
	default MemberResponse.MemberUpdateResponse toUpdateResponse(MemberInfo.Profile info) {
		return MemberResponse.MemberUpdateResponse.builder()
				.id(info.getId())
				.koName(info.getKoName())
				.enName(info.getEnName())
				.email(info.getEmail())
				.phoneNumber(info.getPhoneNumber())
				.isActive(info.getIsActive())
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
				.build();
	}

	// 패스워드 변경 요청 -> 커맨드
	default MemberCommand.PwUpdate toCommand(String id, MemberRequest.PwUpdate request) {
		return MemberCommand.PwUpdate.builder()
				.id(id)
				.currentPw(request.getCurrentPw())
				.newPw(request.getNewPw())
				.build();
	}

    default MemberResponse.MemberPageResponse mapToMemberPage(Page<MemberInfo.Summary> page) {
        return MemberResponse.MemberPageResponse.builder()
                .memberInfos(page == null ? Page.empty() : page)
                .build();
    }
}
