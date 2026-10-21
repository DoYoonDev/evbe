package com.evolve.evolvebe.member.infra;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.member.domain.Member;

public interface MemberJpaRepository extends JpaRepository<Member, String> {
	List<Member> findByIdContaining(String id);

	Page<Member> findByIdContaining(String id, Pageable pageable);
	Page<Member> findByKoNameContaining(String koName, Pageable pageable);
	Page<Member> findByEnNameContaining(String enName, Pageable pageable);
	Page<Member> findByEmailContaining(String email, Pageable pageable);

}
