package com.evolve.evolvebe.member.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
	// 단일 사용자 조회
	Optional<Member> getMember(String userId);

	// 여러 사용자 조회
	List<Member> getMembersByIds(List<String> userIds);

	// 사용자 ID 패턴 검색 (like 검색)
	List<Member> getMembersByIdLike(String id);

	// 회원가입
	void saveMember(Member member);

	// 전체 사용자 조회
	List<Member> findAll();
	// 전체 목록 - 페이징
	Page<Member> findAllPaged(Pageable pageable);
	// 전체 목록 - 페이징 (컬럼별)
	Page<Member> findByIdContaining(String keyword, Pageable pageable);
	Page<Member> findByKoNameContaining(String koName, Pageable pageable);
	Page<Member> findByEnNameContaining(String enName, Pageable pageable);
	Page<Member> findByEmailContaining(String email, Pageable pageable);

	// ID 존재 여부 확인
	boolean existsById(String userId);

	// 사용자 삭제
	void delete(Member member);

	void resetPassword(Member member);
}
