package com.evolve.evolvebe.code.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeService {

	private final CodeRepository codeRepository;
	private final CodeInfoMapper codeInfoMapper;

	public Page<CodeInfo.Main> getCodes(Pageable pageable, CodeCommand.GetCodeList command) {
		Page<Code> codeList = codeRepository.getCodes(pageable, command);
		return codeInfoMapper.mapToMain(codeList);
	}

	public CodeInfo.Main getCode(String codeId) {
		Code code = codeRepository.getCode(codeId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODE));
		return codeInfoMapper.of(code);
	}

	public Page<CodeInfo.CodeGroupInfo> getCodeGroups(
		Pageable pageable,
		CodeCommand.GetCodeGroupList command
	) {
		Page<CodeGroup> codeGroupList = codeRepository.getCodeGroups(pageable, command);
		return codeInfoMapper.mapToGroupInfo(codeGroupList);
	}

	public Page<CodeInfo.Main> getCodesByCodeGroup(
		String codeGroupId,
		Pageable pageable,
		CodeCommand.GetCodeList command
	) {
		codeRepository.getCodeGroup(codeGroupId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODE_GROUP));

		Page<Code> codeList = codeRepository.getCodesByCodeGroup(codeGroupId, pageable, command);
		return codeInfoMapper.mapToMain(codeList);
	}

	public CodeInfo.CodeGroupInfo getCodeGroup(String codeGroupId) {
		CodeGroup codeGroup = codeRepository.getCodeGroup(codeGroupId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODE_GROUP));
		return codeInfoMapper.of(codeGroup);
	}

	public void createCode(CodeCommand.CreateCode command, String codeGroupId) {
		CodeGroup codeGroup = codeRepository.getCodeGroup(codeGroupId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODE_GROUP));
		codeRepository.createCode(command.toEntity(codeGroup));
	}

	public void createCodeGroup(CodeCommand.CreateCodeGroup command) {
		codeRepository.createCodeGroup(command.toEntity());
	}

	public void updateCode(CodeCommand.UpdateCode command, String codeId) {
		Code code = codeRepository.getCode(codeId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODE));

		code.update(
			command.name,
			command.description,
			command.sortOrder,
			command.isActive
		);
	}

	public void updateCodeGroup(CodeCommand.UpdateCodeGroup command, String codeGroupId) {
		CodeGroup codeGroup = codeRepository.getCodeGroup(codeGroupId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CODE));

		codeGroup.update(
			command.name,
			command.description,
			command.isActive
		);
	}

	public void deleteCode(String codeId) {
		codeRepository.deleteCode(codeId);
	}

	public void deleteCodeGroup(String codeGroupId) {
		codeRepository.deleteCodeGroup(codeGroupId);
	}

}
