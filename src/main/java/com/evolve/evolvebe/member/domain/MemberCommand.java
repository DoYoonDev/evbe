package com.evolve.evolvebe.member.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberCommand {

    // 신규 생성
    @Getter
    @Builder
    public static class Create {
        private String id;
        private String koName;
        private String enName;
        private String email;
        private String phoneNumber;
        private String password;
        private Boolean isActive;

        public Member toEntity() {
            return Member.builder()
                    .id(id)
                    .koName(koName)
                    .enName(enName)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .password(password)
                    .isActive(isActive)
                    .build();
        }
    }

    // 수정/변경
    @Getter
    @Builder
    public static class Update {
        private String id;
        private String koName;
        private String enName;
        private String email;
        private String phoneNumber;
        private Boolean isActive;
    }

    // 상태 변경 (활성화/비활성화)
    @Getter
    @Builder
    public static class ChangeStatus {
        private String id;
        private Boolean isActive;
    }

    @Getter
    @Builder
    public static class PwUpdate {
        private String id;
        private String currentPw;
        private String newPw;
    }

    @Getter
    @Builder
    public static class ResetPassword {
        private String id;
        private boolean returnTemp;
        private Delivery delivery;
        public enum Delivery { RETURN, EMAIL }
    }
}
