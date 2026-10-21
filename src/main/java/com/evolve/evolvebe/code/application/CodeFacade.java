package com.evolve.evolvebe.code.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeInfo;
import com.evolve.evolvebe.code.domain.CodeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeFacade {

	private final CodeService codeService;

	@Transactional(readOnly = true)
	public Page<CodeInfo.Main> getCodes(
		Pageable pageable,
		CodeCommand.GetCodeList command
	) {
		return codeService.getCodes(pageable, command);
	}

	@Transactional(readOnly = true)
	public CodeInfo.Main getCode(String codeId) {
		return codeService.getCode(codeId);
	}

	@Transactional(readOnly = true)
	public Page<CodeInfo.CodeGroupInfo> getCodeGroups(
		Pageable pageable,
		CodeCommand.GetCodeGroupList command
	) {
		return codeService.getCodeGroups(pageable, command);
	}

	@Transactional(readOnly = true)
	public CodeInfo.CodeGroupInfo getCodeGroup(String id) {
		return codeService.getCodeGroup(id);
	}

	@Transactional(readOnly = true)
	public Page<CodeInfo.Main> getCodesByCodeGroup(
		String codeGroupId,
		Pageable pageable,
		CodeCommand.GetCodeList command
	) {
		return codeService.getCodesByCodeGroup(codeGroupId, pageable, command);
	}

	@Transactional
	public void createCode(CodeCommand.CreateCode command, String codeGroupId) {
		codeService.createCode(command, codeGroupId);
	}

	@Transactional
	public void createCodeGroup(CodeCommand.CreateCodeGroup command) {
		codeService.createCodeGroup(command);
	}

	@Transactional
	public void updateCode(CodeCommand.UpdateCode command, String codeId) {
		codeService.updateCode(command, codeId);
	}

	@Transactional
	public void updateCodeGroup(CodeCommand.UpdateCodeGroup command, String codeGroupId) {
		codeService.updateCodeGroup(command, codeGroupId);
	}

	@Transactional
	public void deleteCode(String codeId) {
		codeService.deleteCode(codeId);
	}

	@Transactional
	public void deleteCodeGroup(String codeGroupId) {
		codeService.deleteCodeGroup(codeGroupId);
	}

}
