package com.evolve.evolvebe.terms.interfaces;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class TermsResponse {

    @Getter
    @Builder
    @ToString
    public static class TermsDetailResponse {
        private String id;
        private String type;
        private String version;
        private String content;
        private String langCd;
    }
}
