package com.evolve.evolvebe.code.infra;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.evolve.evolvebe.code.domain.Code;
import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeGroup;
import com.evolve.evolvebe.code.domain.CodeRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CodeRepositoryImpl implements CodeRepository {

	private final CodeJpaRepository codeJpaRepository;
	private final CodeGroupJpaRepository codeGroupJpaRepository;

	@Override
	public Page<Code> getCodes(Pageable pageable, CodeCommand.GetCodeList command) {
		return codeJpaRepository.findByIdContainingAndNameContainingAndDescriptionContainingAndIsActive(
			command.getId(),
			command.getName(),
			command.getDescription(),
			command.getIsActive(),
			pageable
		);
	}

	@Override
	public Optional<Code> getCode(String codeId) {
		return codeJpaRepository.findById(codeId);
	}

	@Override
	public Page<CodeGroup> getCodeGroups(Pageable pageable, CodeCommand.GetCodeGroupList command) {
		return codeGroupJpaRepository.findByIdContainingAndNameContainingAndDescriptionContainingAndIsActive(
			command.getId(),
			command.getName(),
			command.getDescription(),
			command.getIsActive(),
			pageable
		);
	}

	@Override
	public Page<Code> getCodesByCodeGroup(
		String codeGroupId,
		Pageable pageable,
		CodeCommand.GetCodeList command
	) {
		return codeJpaRepository.findByCodeGroup_IdAndIdContainingAndNameContainingAndDescriptionContainingAndIsActive(
			codeGroupId,
			command.getId(),
			command.getName(),
			command.getDescription(),
			command.getIsActive(),
			pageable
		);
	}

	@Override
	public Optional<CodeGroup> getCodeGroup(String codeGroupId) {
		return codeGroupJpaRepository.findById(codeGroupId);
	}

	@Override
	public void createCode(Code code) {
		codeJpaRepository.save(code);
	}

	@Override
	public void createCodeGroup(CodeGroup codeGroup) {
		codeGroupJpaRepository.save(codeGroup);
	}

	@Override
	public void deleteCode(String codeId) {
		codeJpaRepository.deleteById(codeId);
	}

	@Override
	public void deleteCodeGroup(String codeGroupId) {
		codeGroupJpaRepository.deleteById(codeGroupId);
	}

}
