package com.evolve.evolvebe.code.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.evolve.evolvebe.code.domain.Code;

import java.util.List;

public interface CodeJpaRepository extends JpaRepository<Code, String> {

    List<Code> findByCodeGroupId(String codeGroupId);

    Page<Code> findByIdContainingAndNameContainingAndDescriptionContainingAndIsActive(
        String id,
        String name,
        String description,
        Boolean isActive,
        Pageable pageable
    );

    Page<Code> findByCodeGroup_IdAndIdContainingAndNameContainingAndDescriptionContainingAndIsActive(
        String codeGroupId,
        String id,
        String name,
        String description,
        Boolean isActive,
        Pageable pageable
    );

}
