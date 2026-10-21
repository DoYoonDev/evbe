package com.evolve.evolvebe.code;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.evolve.evolvebe.code.application.CodeFacade;
import com.evolve.evolvebe.code.domain.Code;
import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeGroup;
import com.evolve.evolvebe.code.domain.CodeInfo;
import com.evolve.evolvebe.code.domain.CodeRepository;
import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CodeIntegrationTest {

	@Autowired
	private CodeFacade codeFacade;

	@Autowired
	private CodeRepository codeRepository;

	@Test
	void getCodes() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeList command = new CodeCommand.GetCodeList();
		command.setId("");
		command.setDescription("");
		command.setName("");
		command.setIsActive(true);

		// when
		Page<CodeInfo.Main> result = codeFacade.getCodes(pageable, command);

		// then
		assertEquals(6, result.getContent().size());
		assertTrue(result.stream().anyMatch(code -> code.getId().equals("TEST_CODE_001")));
	}

	@Nested
	@DisplayName("코드그룹 내 코드목록 조회")
	class GetCodesByCodeGroup {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("getCodesByCodeGroup: 성공")
			void getCodesByCodeGroup() {
				// given
				String codeGroupId = "TEST_GROUP_001";
				Pageable pageable = PageRequest.of(0, 10);
				CodeCommand.GetCodeList command = new CodeCommand.GetCodeList();
				command.setId("");
				command.setDescription("");
				command.setName("");
				command.setIsActive(true);

				// when
				Page<CodeInfo.Main> result = codeFacade.getCodesByCodeGroup(codeGroupId, pageable, command);

				// then
				assertEquals(2, result.getContent().size());
				assertTrue(result.stream().allMatch(code -> code.getCodeGroup().getId().equals(codeGroupId)));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("getCodesByCodeGroup: 코드 그룹이 존재하지 않음")
			void getCodesByCodeGroup() {
				// given
				String codeGroupId = "GROUP_004";
				Pageable pageable = PageRequest.of(0, 10);
				CodeCommand.GetCodeList command = new CodeCommand.GetCodeList();

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeFacade.getCodesByCodeGroup(codeGroupId, pageable, command)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_CODE_GROUP.name(), exception.getCode());
			}
		}
	}

	@Nested
	@DisplayName("코드 생성")
	class CreateCode {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("createCode: 성공")
			void createCode() {
				// given
				CodeCommand.CreateCode createCommand = new CodeCommand.CreateCode();
				createCommand.setId("CODE_007");
				createCommand.setMessageId("MSG_007");
				createCommand.setName("New Code");
				createCommand.setDescription("Description");
				createCommand.setSortOrder(7);
				createCommand.setIsActive(true);

				Pageable pageable = PageRequest.of(0, 10);
				CodeCommand.GetCodeList readCommand = new CodeCommand.GetCodeList();
				readCommand.setId("");
				readCommand.setDescription("");
				readCommand.setName("");
				readCommand.setIsActive(true);

				// when
				codeFacade.createCode(createCommand, "TEST_GROUP_001");

				// then
				Page<CodeInfo.Main> result = codeFacade.getCodes(pageable, readCommand);
				assertEquals(7, result.getContent().size());
				assertTrue(result.stream().anyMatch(code -> code.getId().equals("CODE_007")));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("createCode: 코드그룹이 존재하지 않음")
			void createCode() {
				// given
				String codeGroupId = "GROUP_004";
				CodeCommand.CreateCode command = new CodeCommand.CreateCode();
				command.setId("CODE_007");
				command.setMessageId("MSG_007");
				command.setName("New Code");
				command.setDescription("Description");
				command.setSortOrder(7);
				command.setIsActive(true);

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeFacade.createCode(command, codeGroupId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_CODE_GROUP.name(), exception.getCode());
			}
		}
	}

	@Nested
	@DisplayName("코드 업데이트")
	class UpdateCode {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("updateCode: 성공")
			void updateCode() {
				// given
				CodeCommand.UpdateCode command = new CodeCommand.UpdateCode();
				command.setName("Updated Name");
				command.setDescription("Updated Description");
				command.setSortOrder(3);
				command.setIsActive(false);

				// when
				codeFacade.updateCode(command, "TEST_CODE_001");

				// then
				Code updatedCode = codeRepository.getCode("TEST_CODE_001").orElseThrow();
				assertEquals("Updated Name", updatedCode.getName());
				assertEquals("Updated Description", updatedCode.getDescription());
				assertEquals(false, updatedCode.getIsActive());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("updateCode: 코드가 존재하지 않음")
			void updateCode() {
				// given
				String codeId = "CODE_007";
				CodeCommand.UpdateCode command = new CodeCommand.UpdateCode();
				command.setName("Updated Name");
				command.setDescription("Updated Description");
				command.setSortOrder(3);
				command.setIsActive(false);

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeFacade.updateCode(command, codeId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_CODE.name(), exception.getCode());
			}
		}
	}

	@Nested
	@DisplayName("코드그룹 업데이트")
	class UpdateCodeGroup {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("updateCodeGroup: 성공")
			void updateCodeGroup() {
				// given
				CodeCommand.UpdateCodeGroup command = new CodeCommand.UpdateCodeGroup();
				command.setName("Updated Name");
				command.setDescription("Updated Description");
				command.setIsActive(false);

				// when
				codeFacade.updateCodeGroup(command, "TEST_GROUP_001");

				// then
				CodeGroup updatedCode = codeRepository.getCodeGroup("TEST_GROUP_001").orElseThrow();
				assertEquals("Updated Name", updatedCode.getName());
				assertEquals("Updated Description", updatedCode.getDescription());
				assertEquals(false, updatedCode.getIsActive());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("updateCodeGroup: 코드가 존재하지 않음")
			void updateCodeGroup_코드가_존재하지_않음() {
				// given
				String codeId = "GROUP_004";
				CodeCommand.UpdateCodeGroup command = new CodeCommand.UpdateCodeGroup();
				command.setName("Updated Name");
				command.setDescription("Updated Description");
				command.setIsActive(false);

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeFacade.updateCodeGroup(command, codeId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_CODE.name(), exception.getCode());
			}
		}
	}

	@Test
	void deleteCode() {
		// given
		String codeId = "CODE_001";
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeList command = new CodeCommand.GetCodeList();

		// when
		codeFacade.deleteCode(codeId);

		// then
		Page<CodeInfo.Main> result = codeFacade.getCodes(pageable, command);
		assertFalse(result.stream().anyMatch(code -> code.getId().equals(codeId)));
	}

}
