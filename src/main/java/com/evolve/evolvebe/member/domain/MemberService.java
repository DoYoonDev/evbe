package com.evolve.evolvebe.member.domain;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final MemberInfoMapper memberInfoMapper;
	private final PasswordEncoder passwordEncoder;
	private final TemporaryPasswordGenerator generator;

	// 단건 조회
	public MemberInfo.Main getMember(String userId) {
		Member member = memberRepository.getMember(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
		return memberInfoMapper.mapToMain(member);
	}

	// ID 목록으로 조회
	public List<MemberInfo.Summary> getMembersByIdList(List<String> userIds) {
		List<Member> members = memberRepository.getMembersByIds(userIds);
		return memberInfoMapper.mapToSummaryList(members);
	}

	// ID 유사 검색
	public List<MemberInfo.Summary> getMembersByIdLike(String id) {
		List<Member> members = memberRepository.getMembersByIdLike(id);
		return memberInfoMapper.mapToSummaryList(members);
	}

	// 전체 목록 조회
	@Transactional(readOnly = true)
	public List<MemberInfo.Summary> getAllMembers() {
		List<Member> members = memberRepository.findAll();
		return memberInfoMapper.mapToSummaryList(members);
	}

	// 전체 목록 조회 (page)
	@Transactional(readOnly = true)
	public Page<MemberInfo.Summary> getAllMembersPaged(String keyword, String column, Pageable pageable) {
		Page<Member> members;

		if (isBlank(keyword)) {
			members = memberRepository.findAllPaged(pageable);
		} else {
			SearchColumn col = SearchColumn.from(column); // enum 변환
			members = col.search(memberRepository, keyword, pageable);
		}

		return memberInfoMapper.mapToSummaryPage(members);
	}

	private boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	// 상세 조회
	@Transactional(readOnly = true)
	public MemberInfo.Profile getMemberDetail(String userId) {
		Member member = memberRepository.getMember(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
		return memberInfoMapper.mapToProfile(member);
	}

	// 사용자 등록
	@Transactional
	public MemberInfo.Profile createMember(MemberCommand.Create command) {
		// ID 중복 여부 체크
		if (memberRepository.existsById(command.getId())) {
			throw new CustomException(ErrorCode.EXISTS_ALREADY_MESSAGE);
		}
		// 패스워드 암호화
		String encodedPassword = passwordEncoder.encode(command.getPassword());

		Member member = Member.create(
				command.getId(),
				command.getKoName(),
				command.getEnName(),
				command.getEmail(),
				command.getPhoneNumber(),
				encodedPassword,
				true,
				false
		);

		memberRepository.saveMember(member);
		return memberInfoMapper.mapToProfile(member);
	}

	// 사용자 수정
	@Transactional
	public MemberInfo.Profile updateMember(MemberCommand.Update command) {
		Member member = memberRepository.getMember(command.getId())
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

		// entity 내 수정 메서드 호출
		member.updateInfo(
				command.getKoName(),
				command.getEnName(),
				command.getEmail(),
				command.getPhoneNumber(),
				command.getIsActive()
		);

		return memberInfoMapper.mapToProfile(member);
	}

	// 패스워드 변경
	@Transactional
	public MemberInfo.Profile updatePassword(MemberCommand.PwUpdate command) {
		Member member = memberRepository.getMember(command.getId())
				.orElseThrow(() -> new CustomException(ErrorCode.PASSWORD_NOT_MATCH));

		if (passwordEncoder.matches(command.getNewPw(), member.getPassword())) {
			throw new CustomException(ErrorCode.INVALID_PASSWORD);
		}

		member.updatePw(passwordEncoder.encode(command.getNewPw()));
		memberRepository.saveMember(member);

		return memberInfoMapper.mapToProfile(member);
	}

	public void deleteMember(String id) {
		Member member = memberRepository.getMember(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

		memberRepository.delete(member);
	}

	public void resetPassword(String id) {
		Member member = memberRepository.getMember(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

		//String temp = generator.generate();
		String fixedPassword = "test1234";
		String encoded = passwordEncoder.encode(fixedPassword);

		member.resetPassword(encoded);

		memberRepository.resetPassword(member);
	}
}
