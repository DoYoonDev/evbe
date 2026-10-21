package com.evolve.evolvebe.code.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

	@Mock
	private CodeRepository codeRepository;

	@Mock
	private CodeInfoMapper codeInfoMapper;

	@InjectMocks
	private CodeService codeService;

	@Test
	@DisplayName("코드 목록 조회")
	void getCodeGroups() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeGroupList command = mock(CodeCommand.GetCodeGroupList.class);
		Page<CodeGroup> codeGroupList = new PageImpl<>(
			List.of(mock(CodeGroup.class), mock(CodeGroup.class))
		);
		Page<CodeInfo.CodeGroupInfo> codeGroupInfo = new PageImpl<>(
			List.of(mock(CodeInfo.CodeGroupInfo.class), mock(CodeInfo.CodeGroupInfo.class))
		);

		when(codeRepository.getCodeGroups(pageable, command)).thenReturn(codeGroupList);
		when(codeInfoMapper.mapToGroupInfo(codeGroupList)).thenReturn(codeGroupInfo);

		// when
		Page<CodeInfo.CodeGroupInfo> result = codeService.getCodeGroups(pageable, command);

		// then
		assertEquals(2, result.getTotalElements());
		verify(codeRepository).getCodeGroups(pageable, command);
		verify(codeInfoMapper).mapToGroupInfo(codeGroupList);
	}

	@Nested
	@DisplayName("코드그룹 내 코드목록 조회")
	class GetCode {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("getCode: 성공")
			void getCode() {
				// given
				String codeId = "code001";
				Code code = mock(Code.class);
				when(codeRepository.getCode(codeId)).thenReturn(
					Optional.of(code)
				);
				when(codeInfoMapper.of(code)).thenReturn(mock(CodeInfo.Main.class));

				// when
				CodeInfo.Main result = codeService.getCode(codeId);

				// then
				assertNotNull(result);
				verify(codeRepository).getCode(codeId);
				verify(codeInfoMapper).of(code);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("getCode: 존재하지 않는 코드 ID")
			void getCode_존재하지_않는_코드_ID() {
				// given
				String codeId = "invalidCode";
				when(codeRepository.getCode(codeId))
					.thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeService.getCode(codeId)
				);

				// then
				assertEquals(
					ErrorCode.NOT_FOUND_CODE.name(),
					exception.getCode()
				);
				verify(codeRepository).getCode(codeId);
				verifyNoInteractions(codeInfoMapper);
			}
		}
	}

	@Test
	@DisplayName("코드 목록 조회")
	void getCodes() {
		// given
		Pageable pageable = PageRequest.of(0, 10);
		CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);
		Page<Code> codeGroupList = new PageImpl<>(
			List.of(mock(Code.class), mock(Code.class))
		);
		Page<CodeInfo.Main> codeInfo = new PageImpl<>(
			List.of(mock(CodeInfo.Main.class), mock(CodeInfo.Main.class))
		);
		when(codeRepository.getCodes(pageable, command)).thenReturn(codeGroupList);
		when(codeInfoMapper.mapToMain(codeGroupList)).thenReturn(codeInfo);

		// when
		Page<CodeInfo.Main> result = codeService.getCodes(pageable, command);

		// then
		assertEquals(2, result.getSize());
		verify(codeRepository).getCodes(pageable, command);
		verify(codeInfoMapper).mapToMain(codeGroupList);
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
				String codeGroupId = "codeGroup1";
				Pageable pageable = PageRequest.of(0, 10);
				Page<Code> codeList = new PageImpl<>(
					List.of(mock(Code.class), mock(Code.class))
				);
				CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);
				Page<CodeInfo.Main> codeInfo = new PageImpl<>(
					List.of(mock(CodeInfo.Main.class), mock(CodeInfo.Main.class))
				);

				when(codeRepository.getCodeGroup(codeGroupId)).thenReturn(
					Optional.of(mock(CodeGroup.class))
				);
				when(codeRepository.getCodesByCodeGroup(codeGroupId, pageable, command)).thenReturn(codeList);
				when(codeInfoMapper.mapToMain(codeList)).thenReturn(codeInfo);

				// when
				Page<CodeInfo.Main> result = codeService.getCodesByCodeGroup(codeGroupId, pageable, command);

				// then
				assertEquals(2, result.getSize());
				verify(codeRepository).getCodeGroup(codeGroupId);
				verify(codeRepository).getCodesByCodeGroup(codeGroupId, pageable, command);
				verify(codeInfoMapper).mapToMain(codeList);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("getCodesByCodeGroup: 존재하지 않는 코드그룹 ID")
			void getCodesByCodeGroup_존재하지_않는_코드그룹_ID() {
				// given
				String codeGroupId = "invalidGroup";
				when(codeRepository.getCodeGroup(codeGroupId))
					.thenReturn(Optional.empty());
				Pageable pageable = PageRequest.of(0, 10);
				CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeService.getCodesByCodeGroup(codeGroupId, pageable, command)
				);

				// then
				assertEquals(
					ErrorCode.NOT_FOUND_CODE_GROUP.name(),
					exception.getCode()
				);
				verify(codeRepository).getCodeGroup(codeGroupId);
				verifyNoInteractions(codeInfoMapper);
			}
		}
	}

	@Nested
	@DisplayName("코드그룹 정보 조회")
	class GetCodeGroup {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("getCodeGroup: 성공")
			void getCodeGroup() {
				// given
				String codeGroupId = "codeGroup1";
				CodeGroup codeGroup = mock(CodeGroup.class);
				when(codeRepository.getCodeGroup(codeGroupId)).thenReturn(Optional.of(codeGroup));
				when(codeInfoMapper.of(codeGroup)).thenReturn(mock(CodeInfo.CodeGroupInfo.class));

				// when
				CodeInfo.CodeGroupInfo result = codeService.getCodeGroup(codeGroupId);

				// then
				assertNotNull(result);
				verify(codeRepository).getCodeGroup(codeGroupId);
				verify(codeInfoMapper).of(codeGroup);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("getCodeGroup: 존재하지 않는 코드그룹 ID")
			void getCodeGroup_존재하지_않는_코드그룹_ID() {
				// given
				String codeGroupId = "invalidGroup";
				when(codeRepository.getCodeGroup(codeGroupId))
					.thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeService.getCodeGroup(codeGroupId)
				);

				// then
				assertEquals(
					ErrorCode.NOT_FOUND_CODE_GROUP.name(),
					exception.getCode()
				);
				verify(codeRepository).getCodeGroup(codeGroupId);
				verifyNoInteractions(codeInfoMapper);
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
				String codeGroupId = "group001";
				CodeCommand.CreateCode command = mock(CodeCommand.CreateCode.class);
				CodeGroup codeGroup = mock(CodeGroup.class);
				when(codeRepository.getCodeGroup(codeGroupId)).thenReturn(Optional.of(codeGroup));

				// when
				codeService.createCode(command, codeGroupId);

				// then
				verify(codeRepository).getCodeGroup(codeGroupId);
				verify(codeRepository).createCode(command.toEntity(codeGroup));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("createCode: 존재하지 않는 코드그룹 ID")
			void createCode_존재하지_않는_코드그룹_ID() {
				// given
				String codeGroupId = "invalidGroup";
				CodeCommand.CreateCode command = mock(CodeCommand.CreateCode.class);
				when(codeRepository.getCodeGroup(codeGroupId))
					.thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeService.createCode(command, codeGroupId)
				);

				// then
				assertEquals(
					ErrorCode.NOT_FOUND_CODE_GROUP.name(),
					exception.getCode()
				);
				verify(codeRepository).getCodeGroup(codeGroupId);
				verifyNoMoreInteractions(codeRepository);
			}
		}
	}

	@Test
	@DisplayName("코드그룹 생성 성공")
	void createCodeGroup() {
		// given
		CodeCommand.CreateCodeGroup command = mock(CodeCommand.CreateCodeGroup.class);

		// when
		codeService.createCodeGroup(command);

		// then
		verify(codeRepository).createCodeGroup(command.toEntity());
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
				String codeId = "invalidCode";

				Code code = mock(Code.class);
				when(codeRepository.getCode(codeId)).thenReturn(Optional.of(code));

				// when
				codeService.updateCode(command, codeId);

				// then
				verify(codeRepository).getCode("invalidCode");
				verify(code).update(
					command.getName(),
					command.getDescription(),
					command.getSortOrder(),
					command.getIsActive()
				);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("updateCode: 코드 없음")
			void updateCode_코드_없음() {
				// given
				String codeId = "invalidCode";
				CodeCommand.UpdateCode command = new CodeCommand.UpdateCode();
				when(codeRepository.getCode("invalidCode"))
					.thenReturn(Optional.empty());

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeService.updateCode(command, codeId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_CODE.name(), exception.getCode());
				verify(codeRepository).getCode("invalidCode");
				verifyNoMoreInteractions(codeRepository);
			}
		}
	}

	@Nested
	@DisplayName("코드 삭제")
	class DeleteCode {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("deleteCode: 성공")
			void deleteCode() {
				// given
				String codeId = "code1";

				// when
				codeService.deleteCode(codeId);

				// then
				verify(codeRepository).deleteCode(codeId);
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("deleteCode: 코드 없음")
			void deleteCode_코드_없음() {
				// given
				String codeId = "invalidCode";
				doThrow(new CustomException(ErrorCode.NOT_FOUND_CODE))
					.when(codeRepository).deleteCode(codeId);

				// when
				CustomException exception = assertThrows(CustomException.class, () ->
					codeService.deleteCode(codeId)
				);

				// then
				assertEquals(ErrorCode.NOT_FOUND_CODE.name(), exception.getCode());
				verify(codeRepository).deleteCode(codeId);
			}
		}
	}

}