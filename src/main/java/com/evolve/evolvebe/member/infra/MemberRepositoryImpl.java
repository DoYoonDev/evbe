package com.evolve.evolvebe.member.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.evolve.evolvebe.member.domain.Member;
import com.evolve.evolvebe.member.domain.MemberRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final MemberJpaRepository memberJpaRepository;

	@Override
	public Optional<Member> getMember(String userId) {
		return memberJpaRepository.findById(userId);
	}

	@Override
	public List<Member> getMembersByIds(List<String> userIds) {
		return memberJpaRepository.findAllById(userIds);
	}

	@Override
	public List<Member> getMembersByIdLike(String id) {
		return memberJpaRepository.findByIdContaining(id);
	}

	// 전체 사용자 조회
	@Override
	public List<Member> findAll() {
		return memberJpaRepository.findAll();
	}

	@Override
	public Page<Member> findAllPaged(Pageable pageable) {
		return memberJpaRepository.findAll(pageable);
	}

	@Override
	public Page<Member> findByIdContaining(String keyword, Pageable pageable) {
		return memberJpaRepository.findByIdContaining(keyword, pageable);
	}

	@Override
	public Page<Member> findByKoNameContaining(String keyword, Pageable pageable) {
		return memberJpaRepository.findByKoNameContaining(keyword, pageable);
	}

	@Override
	public Page<Member> findByEnNameContaining(String keyword, Pageable pageable) {
		return memberJpaRepository.findByEnNameContaining(keyword, pageable);
	}

	@Override
	public Page<Member> findByEmailContaining(String keyword, Pageable pageable) {
		return memberJpaRepository.findByEmailContaining(keyword, pageable);
	}

	@Override
	public boolean existsById(String userId) {
		return memberJpaRepository.existsById(userId);
	}

	// 회원가입
	@Override
	public void saveMember(Member member) {
		memberJpaRepository.save(member);
	}

	@Override
	public void delete(Member member) {
		memberJpaRepository.delete(member);
	}

	@Override
	public void resetPassword(Member member) {
		memberJpaRepository.save(member);
	}
}
