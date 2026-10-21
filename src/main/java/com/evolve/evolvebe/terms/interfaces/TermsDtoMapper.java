package com.evolve.evolvebe.terms.interfaces;

import com.evolve.evolvebe.terms.domain.TermsCommand;
import com.evolve.evolvebe.terms.domain.TermsInfo;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface TermsDtoMapper {
    TermsCommand.GetTerms fromTos(TermsRequest.GetTosRequest request);

    TermsResponse.TermsDetailResponse mapToResponse(TermsInfo.Main result);

    TermsCommand.GetTerms fromTop(TermsRequest.GetTopRequest request);

    TermsResponse.TermsDetailResponse mapTop(TermsInfo.Main result);

    TermsCommand.GetTermsMember fromTermsMember(TermsRequest.TermsMember termsMemberReq);
}
