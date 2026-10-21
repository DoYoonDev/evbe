package com.evolve.evolvebe.member.application;

import java.util.List;

import com.evolve.evolvebe.member.domain.MemberCommand;
import com.evolve.evolvebe.role.domain.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.evolve.evolvebe.member.domain.MemberInfo;
import com.evolve.evolvebe.member.domain.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFacade {

	private final MemberService memberService;
	private final RoleService roleService;

	// 사용자 단건 조회 (Main)
	@Transactional(readOnly = true)
	public MemberInfo.Main getMember(String userId) {
		return memberService.getMember(userId);
	}

	// 사용자 상세 조회 (Profile)
	@Transactional(readOnly = true)
	public MemberInfo.Profile getMemberDetail(String userId) {
		return memberService.getMemberDetail(userId);
	}

	// 전체 사용자 목록 조회
	@Transactional(readOnly = true)
	public List<MemberInfo.Summary> getAllMembers() {
		return memberService.getAllMembers();
	}

	// 전체 사용자 목록 조회 (Page)
	@Transactional(readOnly = true)
	public Page<MemberInfo.Summary> getAllMembersPaged(String keyword, String column, Pageable pageable) {
		return memberService.getAllMembersPaged(keyword, column, pageable);
	}

	// ID 유사 검색
	@Transactional(readOnly = true)
	public List<MemberInfo.Summary> searchMembersById(String idKeyword) {
		return memberService.getMembersByIdLike(idKeyword);
	}

	// 사용자 등록
	@Transactional
	public MemberInfo.Profile createMember(MemberCommand.Create command) {
		return memberService.createMember(command);
	}

	// 사용자 수정
	@Transactional
	public MemberInfo.Profile updateMember(MemberCommand.Update command) {
		return memberService.updateMember(command);
	}

	// 패스워드 변경
	@Transactional
	public MemberInfo.Profile updatePw(MemberCommand.PwUpdate command) {
		return memberService.updatePassword(command);
	}

	// 사용자 삭제
	@Transactional
	public void deleteMember(String id) {
		roleService.removeAllRolesOfMember(id);
		memberService.deleteMember(id);
	}

	// 사용자 패스워드 초기화
	@Transactional
	public void resetPassword(String id) {
		memberService.resetPassword(id);
	}
}
