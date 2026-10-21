package com.evolve.evolvebe.terms.interfaces;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TermsRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetTosRequest {
        private String id;
        private String type = "SERVICE";
        private String version;
        private String content;
        private String langCd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetTopRequest {
        private String id;
        private String type = "PRIVACY";
        private String version;
        private String content;
        private String langCd;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TermsMember {
        private String id;
        private String memberId;
        private String termsId;
        private boolean agreed;
    }
}
