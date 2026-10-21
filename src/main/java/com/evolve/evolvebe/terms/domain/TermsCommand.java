package com.evolve.evolvebe.terms.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class TermsCommand {

    @Getter
    @Setter
    @Builder
    public static class GetTerms {
        private String id;
        private String type;
        private String version;
        private String content;
        private String langCd;
    }

    @Getter
    @Setter
    @Builder
    public static class GetTermsMember {
        private String id;
        private String memberId;
        private String termsId;
        private boolean agreed;

        public TermsMember toEntity() {
            return TermsMember.builder()
                    .memberId(memberId)
                    .termsId(termsId)
                    .agreed(agreed)
                    .build();
        }
    }
}
