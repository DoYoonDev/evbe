package com.evolve.evolvebe.member.interfaces;

import lombok.*;

public class MemberRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = "password")
    public static class Create {
        private String id;
        private String koName;
        private String enName;
        private String email;
        private String phoneNumber;
        private String password;
        private Boolean isActive;
        private TermsAgreed termsAgreed;
        private String tosId;
        private String topId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        private String koName;
        private String enName;
        private String email;
        private String phoneNumber;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PwUpdate {
        private String currentPw;
        private String newPw;
    }

    @Getter
    @Setter
    @Builder
    @ToString
    public static class TermsAgreed {
        private boolean service;
        private boolean privacy;
    }

}
