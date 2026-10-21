package com.evolve.evolvebe.code.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.code.domain.CodeGroup;

public interface CodeGroupJpaRepository extends JpaRepository<CodeGroup, String> {
	Page<CodeGroup> findByIdContainingAndNameContainingAndDescriptionContainingAndIsActive(
		String id,
		String name,
		String description,
		Boolean isActive,
		Pageable pageable
	);
}
