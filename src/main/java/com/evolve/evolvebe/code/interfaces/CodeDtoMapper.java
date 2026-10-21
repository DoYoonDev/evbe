package com.evolve.evolvebe.code.interfaces;

import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(
    componentModel = "spring",
    injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface CodeDtoMapper {

    CodeCommand.CreateCode of(CodeRequest.CreateCode request);

    CodeCommand.CreateCodeGroup of(CodeRequest.CreateCodeGroup request);

    CodeCommand.UpdateCode of(CodeRequest.UpdateCode request);

    CodeCommand.UpdateCodeGroup of(CodeRequest.UpdateCodeGroup request);

    CodeCommand.GetCodeList of(CodeRequest.GetCodeList request);

    CodeCommand.GetCodeGroupList of(CodeRequest.GetCodeGroupList request);

    @Mapping(source="result" , target="codeInfo")
    CodeResponse.CodeInfoResponse of(CodeInfo.Main result);

    @Mapping(source="result" , target="codeInfo")
    CodeResponse.CodeGroupInfoResponse of(CodeInfo.CodeGroupInfo result);

    default CodeResponse.CodeGroupListResponse mapToGroupList(Page<CodeInfo.CodeGroupInfo> result) {
        return CodeResponse.CodeGroupListResponse.builder()
            .codeInfos(result)
            .build();
    }

    default CodeResponse.CodeListResponse mapToCodeList(Page<CodeInfo.Main> result) {
        return CodeResponse.CodeListResponse.builder()
            .codeInfos(result)
            .build();
    }

}
