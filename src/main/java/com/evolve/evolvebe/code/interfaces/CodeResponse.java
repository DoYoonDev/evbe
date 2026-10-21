package com.evolve.evolvebe.code.interfaces;

import org.springframework.data.domain.Page;

import com.evolve.evolvebe.code.domain.CodeInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class CodeResponse {

    @Getter
    @Builder
    @ToString
    public static class CodeListResponse {
        private Page<CodeInfo.Main> codeInfos;
    }

    @Getter
    @Builder
    @ToString
    public static class CodeInfoResponse {
        private CodeInfo.Main codeInfo;
    }

    @Getter
    @Builder
    @ToString
    public static class CodeGroupListResponse {
        private Page<CodeInfo.CodeGroupInfo> codeInfos;
    }

    @Getter
    @Builder
    @ToString
    public static class CodeGroupInfoResponse {
        private CodeInfo.CodeGroupInfo codeInfo;
    }

}
