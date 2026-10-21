package com.evolve.evolvebe.terms.domain;

import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface TermsInfoMapper {
    TermsInfo.Main fromTos(Terms terms);

    TermsInfo.Main fromTop(Terms terms);

    TermsInfo.TermsMember fromTermsMember(TermsMember termsMember);
}
