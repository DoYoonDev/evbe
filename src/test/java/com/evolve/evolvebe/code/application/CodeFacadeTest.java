package com.evolve.evolvebe.code.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeInfo;
import com.evolve.evolvebe.code.domain.CodeService;

@ExtendWith(MockitoExtension.class)
class CodeFacadeTest {

	@Mock
	private CodeService codeService;

	@InjectMocks
	private CodeFacade codeFacade;

	@Test
	@DisplayName("코드목록 조회 성공")
	void getCodes() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);
		Page<CodeInfo.Main> codeInfo = new PageImpl<>(
			List.of(mock(CodeInfo.Main.class), mock(CodeInfo.Main.class))
		);
		when(codeService.getCodes(pageable, command)).thenReturn(codeInfo);

		// when
		Page<CodeInfo.Main> result = codeFacade.getCodes(pageable, command);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(codeInfo.getSize());
		verify(codeService).getCodes(pageable, command);
	}

	@Test
	@DisplayName("코드 상세조회 성공")
	void getCode() {
		// given
		String codeId = "code001";
		CodeInfo.Main codeInfo = mock(CodeInfo.Main.class);
		when(codeService.getCode(codeId)).thenReturn(codeInfo);

		// when
		CodeInfo.Main result = codeFacade.getCode(codeId);

		// then
		assertThat(result).isNotNull();
		verify(codeService).getCode(codeId);
	}

	@Test
	@DisplayName("코드그룹 목록 조회 성공")
	void getCodeGroups() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeGroupList command = mock(CodeCommand.GetCodeGroupList.class);
		Page<CodeInfo.CodeGroupInfo> codeGroupInfo = new PageImpl<>(
			List.of(mock(CodeInfo.CodeGroupInfo.class), mock(CodeInfo.CodeGroupInfo.class))
		);
		when(codeService.getCodeGroups(pageable, command)).thenReturn(codeGroupInfo);

		// when
		Page<CodeInfo.CodeGroupInfo> result = codeFacade.getCodeGroups(pageable, command);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(codeGroupInfo.getSize());
		verify(codeService).getCodeGroups(pageable, command);
	}

	@Test
	@DisplayName("코드그룹 내 코드목록 조회 성공")
	void getCodesByCodeGroup() {
		// given
		String codeGroupId = "test";
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);
		Page<CodeInfo. Main> codeInfo = new PageImpl<>(
			List.of(mock(CodeInfo.Main.class), mock(CodeInfo.Main.class))
		);
		when(codeService.getCodesByCodeGroup(codeGroupId, pageable, command)).thenReturn(codeInfo);

		// when
		Page<CodeInfo.Main> result = codeFacade.getCodesByCodeGroup(codeGroupId, pageable, command);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(codeInfo.getSize());
		verify(codeService).getCodesByCodeGroup(codeGroupId, pageable, command);
	}

	@Test
	@DisplayName("코드생성 성공")
	void createCode() {
		// given
		String codeGroupId = "group001";
		CodeCommand.CreateCode command = new CodeCommand.CreateCode();
		command.setId("code001");
		command.setMessageId("MSG001");
		command.setName("test");
		command.setDescription("test");
		command.setSortOrder(1);
		command.setIsActive(true);

		// when
		codeFacade.createCode(command, codeGroupId);

		// then
		verify(codeService).createCode(command, codeGroupId);
	}

	@Test
	@DisplayName("코드그룹 생성 성공")
	void createCodeGroup() {
		// given
		CodeCommand.CreateCodeGroup command = new CodeCommand.CreateCodeGroup();
		command.setId("code001");
		command.setName("test");
		command.setDescription("test");
		//command.setSortOrder(1);
		command.setIsActive(true);

		// when
		codeFacade.createCodeGroup(command);

		// then
		verify(codeService).createCodeGroup(command);
	}

	@Test
	@DisplayName("코드수정 성공")
	void updateCode() {
		// given
		String codeId = "code001";
		CodeCommand.UpdateCode command = new CodeCommand.UpdateCode();
		command.setName("test");
		command.setDescription("test");
		command.setSortOrder(1);
		command.setIsActive(true);

		// when
		codeFacade.updateCode(command, codeId);

		// then
		verify(codeService).updateCode(command,codeId);
	}

	@Test
	@DisplayName("코드삭제 성공")
	void deleteCode() {
		// given
		String codeId = "test";

		// when
		codeFacade.deleteCode(codeId);

		// then
		verify(codeService).deleteCode(codeId);
	}

}