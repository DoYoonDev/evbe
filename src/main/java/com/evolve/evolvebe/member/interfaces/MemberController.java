package com.evolve.evolvebe.member.interfaces;

import java.util.List;

import com.evolve.evolvebe.member.domain.MemberCommand;
import com.evolve.evolvebe.role.application.RoleFacade;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.interfaces.RoleDtoMapper;
import com.evolve.evolvebe.terms.application.TermsFacade;
import com.evolve.evolvebe.terms.domain.TermsCommand;
import com.evolve.evolvebe.terms.domain.TermsInfo;
import com.evolve.evolvebe.terms.interfaces.TermsDtoMapper;
import com.evolve.evolvebe.terms.interfaces.TermsRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.evolve.evolvebe.member.application.MemberFacade;
import com.evolve.evolvebe.member.domain.MemberInfo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final MemberFacade memberFacade;
	private final MemberDtoMapper memberDtoMapper;
	private final RoleDtoMapper roleDtoMapper;
	private final RoleFacade roleFacade;
	private final TermsFacade termsFacade;
	private final TermsDtoMapper termsDtoMapper;

	// 전체 사용자 조회
	@GetMapping
	public ResponseEntity<MemberResponse.MemberListResponse> getAllMembers() {
		List<MemberInfo.Summary> result = memberFacade.getAllMembers();
		MemberResponse.MemberListResponse response = memberDtoMapper.mapToMemberList(result);
		return ResponseEntity.ok(response);
	}

	// 전체 사용자 조회 (페이징)
	@GetMapping("/page")
	public ResponseEntity<MemberResponse.MemberPageResponse> getMembersPaged(
			@RequestParam(required = false) String column,
			@RequestParam(required = false) String keyword,
			@PageableDefault(sort = {"createdAt", "id"}, direction = Sort.Direction.DESC, size = 10)
			Pageable pageable
	) {
		Page<MemberInfo.Summary> result = memberFacade.getAllMembersPaged(keyword, column, pageable);
		MemberResponse.MemberPageResponse response = memberDtoMapper.mapToMemberPage(result);
		return ResponseEntity.ok(response);
	}

	// 회원가입 ID 중복체크
	@GetMapping("/chkId/{userId}")
	public ResponseEntity<Boolean>  checkDuplicateUserId(
		@PathVariable("userId") String id
	) {
		List<MemberInfo.Summary> result = memberFacade.searchMembersById(id);
		boolean isDuplicate = !result.isEmpty();
		return ResponseEntity.ok(isDuplicate);
	}

	// 사용자 상세 조회
	@GetMapping("/{id}")
	public ResponseEntity<MemberResponse.MemberDetailResponse> getMemberById(@PathVariable("id") String id) {
		MemberInfo.Profile result = memberFacade.getMemberDetail(id);
		MemberResponse.MemberDetailResponse response = memberDtoMapper.toDetailResponse(result);
		return ResponseEntity.ok(response);
	}

	// ID 유사 검색 (like)
	@GetMapping("/search/{id}")
	public ResponseEntity<MemberResponse.MemberListResponse> getMembersByIdLike(@PathVariable("id") String id) {
		List<MemberInfo.Summary> result = memberFacade.searchMembersById(id);
		MemberResponse.MemberListResponse response = memberDtoMapper.mapToMemberList(result);
		return ResponseEntity.ok(response);
	}

	// 사용자 등록
	@PostMapping("/create")
	public ResponseEntity<Boolean> createMember(
			@RequestBody MemberRequest.Create request
	) {
		TermsRequest.TermsMember termsMemberReq = new TermsRequest.TermsMember();

		MemberCommand.Create command = memberDtoMapper.toCommand(request);
		MemberInfo.Profile memberInfo = memberFacade.createMember(command);

		// 권한 등록
		if (memberInfo != null) {
			String roleId = "ROLE_USER";
			String memberId = request.getId();

			RoleCommand.AddMember roleCommand = roleDtoMapper.mapToAddCommand(roleId, memberId);
			roleFacade.addMemberToRole(roleCommand);
		}

		if (request.getTermsAgreed().isService()) {
			termsMemberReq.setMemberId(request.getId());
			termsMemberReq.setTermsId(request.getTosId());
			termsMemberReq.setAgreed(request.getTermsAgreed().isService());
			TermsCommand.GetTermsMember com = termsDtoMapper.fromTermsMember(termsMemberReq);
			termsFacade.createTermsMember(com);
		}
		if (request.getTermsAgreed().isPrivacy()) {
			termsMemberReq.setMemberId(request.getId());
			termsMemberReq.setTermsId(request.getTopId());
			termsMemberReq.setAgreed(request.getTermsAgreed().isPrivacy());
			TermsCommand.GetTermsMember com = termsDtoMapper.fromTermsMember(termsMemberReq);
			termsFacade.createTermsMember(com);
		}

		return ResponseEntity.ok(Boolean.TRUE);
	}

	// 사용자 수정
	@PutMapping("/{id}")
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MemberResponse.MemberUpdateResponse> updateMember(
			@PathVariable("id") String id,
			@RequestBody MemberRequest.Update request
	) {
		MemberCommand.Update command = memberDtoMapper.toCommand(id, request);
		MemberInfo.Profile updated = memberFacade.updateMember(command);
		MemberResponse.MemberUpdateResponse response = memberDtoMapper.toUpdateResponse(updated);
		return ResponseEntity.ok(response);
	}

	// 패스워드 변경
	@PutMapping("/updatePw/{id}")
	public ResponseEntity<MemberResponse.MemberUpdateResponse> updatePw(
			@PathVariable("id") String id,
			@RequestBody MemberRequest.PwUpdate request
	) {

		MemberCommand.PwUpdate command = memberDtoMapper.toCommand(id, request);
        memberFacade.updatePw(command);
		//MemberInfo.Profile updated = memberFacade.updatePw(command);
		//MemberResponse.MemberUpdateResponse response = memberDtoMapper.toUpdateResponse(updated);
		//return ResponseEntity.ok(response);
        return ResponseEntity.noContent().build();
	}

	// 사용자 삭제
	@DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteMember(@PathVariable String id) {
		memberFacade.deleteMember(id);
		return ResponseEntity.noContent().build(); // 204 No Content
	}

	@PostMapping("/{id}/password/reset")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> resetPassword(@PathVariable String id) {
		memberFacade.resetPassword(id);
		return ResponseEntity.noContent().build();
	}
}
