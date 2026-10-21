package com.evolve.evolvebe.terms.infra;

import com.evolve.evolvebe.terms.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsJpaRepository extends JpaRepository<Terms, Integer> {
    Terms findFirstByTypeAndLangCdOrderByVersionDesc(String type, String langCd);
}
