package com.evolve.evolvebe.terms.infra;

import com.evolve.evolvebe.terms.domain.TermsMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsMemberJpaRepository extends JpaRepository<TermsMember, Integer> {
}
