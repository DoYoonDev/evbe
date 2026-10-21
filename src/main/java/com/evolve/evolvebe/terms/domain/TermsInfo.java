package com.evolve.evolvebe.terms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

public class TermsInfo {

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    public static class Main {
        private final String id;
        private final String type;
        private final String version;
        private final String content;
        private final String langCd;
        private final ZonedDateTime createdAt;
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    public static class TermsMember {
        private final String id;
        private final String memberId;
        private final String termsId;
        private final boolean agreed;
        private final ZonedDateTime agreedAt;
    }
}
