package com.evolve.evolvebe.terms.application;

import com.evolve.evolvebe.terms.domain.TermsCommand;
import com.evolve.evolvebe.terms.domain.TermsInfo;
import com.evolve.evolvebe.terms.domain.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TermsFacade {

    private final TermsService termsService;

    @Transactional
    public void createTermsMember(TermsCommand.GetTermsMember com) {
        termsService.createTermsMember(com);
    }

    @Transactional(readOnly = true)
    public TermsInfo.Main getTos(TermsCommand.GetTerms command) {
        return termsService.getTos(command);
    }

    @Transactional(readOnly = true)
    public TermsInfo.Main getTop(TermsCommand.GetTerms command) {
        return termsService.getTop(command);
    }
}
