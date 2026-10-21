package com.evolve.evolvebe.terms.infra;

import com.evolve.evolvebe.terms.domain.Terms;
import com.evolve.evolvebe.terms.domain.TermsCommand;
import com.evolve.evolvebe.terms.domain.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TermsRepositoryImpl implements TermsRepository {

    private final TermsJpaRepository termsJpaRepository;
    private final TermsMemberJpaRepository termsMemberJpaRepository;

    @Override
    public Terms getTos(TermsCommand.GetTerms command) {
        return termsJpaRepository.findFirstByTypeAndLangCdOrderByVersionDesc(
                command.getType(), command.getLangCd()
        );
    }

    @Override
    public Terms getTop(TermsCommand.GetTerms command) {
        return termsJpaRepository.findFirstByTypeAndLangCdOrderByVersionDesc(
                command.getType(), command.getLangCd()
        );
    }

    @Override
    public void createTermsMember(TermsCommand.GetTermsMember com) {
        termsMemberJpaRepository.save(com.toEntity());
    }
}
