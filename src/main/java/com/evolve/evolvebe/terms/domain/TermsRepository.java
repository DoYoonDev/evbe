package com.evolve.evolvebe.terms.domain;

public interface TermsRepository {
    Terms getTos(TermsCommand.GetTerms command);

    Terms getTop(TermsCommand.GetTerms command);

    void createTermsMember(TermsCommand.GetTermsMember com);
}
