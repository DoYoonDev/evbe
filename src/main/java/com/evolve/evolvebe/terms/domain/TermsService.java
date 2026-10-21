package com.evolve.evolvebe.terms.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;
    private final TermsInfoMapper termsInfoMapper;

    @Transactional(readOnly = true)
    public TermsInfo.Main getTos(TermsCommand.GetTerms command) {
        Terms terms = termsRepository.getTos(command);
        return termsInfoMapper.fromTos(terms);
    }

    @Transactional(readOnly = true)
    public TermsInfo.Main getTop(TermsCommand.GetTerms command) {
        Terms terms = termsRepository.getTop(command);
        return termsInfoMapper.fromTop(terms);
    }

    @Transactional
    public void createTermsMember(TermsCommand.GetTermsMember com) {
        termsRepository.createTermsMember(com);
    }
}
