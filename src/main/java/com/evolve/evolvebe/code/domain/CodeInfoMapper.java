package com.evolve.evolvebe.code.domain;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(
    componentModel = "spring",
    injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface CodeInfoMapper {

    CodeInfo.Main of(Code code);

    CodeInfo.CodeGroupInfo of(CodeGroup codeGroup);

    @Mapping(target = "content", source = "content")
    List<CodeInfo.CodeGroupInfo> mapToGroupInfoList(List<CodeGroup> codes);

    default Page<CodeInfo.CodeGroupInfo> mapToGroupInfo(Page<CodeGroup> codeList) {
        List<CodeInfo.CodeGroupInfo> mainList = mapToGroupInfoList(codeList.getContent());
        return new PageImpl<>(mainList, codeList.getPageable(), codeList.getTotalElements());
    }

    @Mapping(target = "content", source = "content")
    List<CodeInfo.Main> mapToMainList(List<Code> codes);

    default Page<CodeInfo.Main> mapToMain(Page<Code> codeList) {
        List<CodeInfo.Main> mainList = mapToMainList(codeList.getContent());
        return new PageImpl<>(mainList, codeList.getPageable(), codeList.getTotalElements());
    }

}
