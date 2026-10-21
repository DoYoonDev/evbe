package com.evolve.evolvebe.code.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CodeRepository {

	Page<Code> getCodes(Pageable pageable, CodeCommand.GetCodeList command);
	Optional<Code> getCode(String codeId);
	Page<CodeGroup> getCodeGroups(Pageable pageable, CodeCommand.GetCodeGroupList command);
	Page<Code> getCodesByCodeGroup(String codeGroupId, Pageable pageable, CodeCommand.GetCodeList command);
	Optional<CodeGroup> getCodeGroup(String codeGroupId);
	void createCode(Code code);
	void createCodeGroup(CodeGroup codeGroup);
	void deleteCode(String codeId);
	void deleteCodeGroup(String codeGroupId);

}
