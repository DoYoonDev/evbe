package com.evolve.evolvebe.code.interfaces;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.code.application.CodeFacade;
import com.evolve.evolvebe.code.domain.CodeCommand;
import com.evolve.evolvebe.code.domain.CodeInfo;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = CodeController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = {SecurityConfig.class, JwtFilter.class}
		)
	}
)
@AutoConfigureRestDocs
class CodeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockitoBean
	private CodeFacade codeFacade;

	@MockitoBean
	private CodeDtoMapper codeDtoMapper;

	@Test
	@DisplayName("코드 목록 조회 성공")
	void getCodes() throws Exception {
		// given
		CodeRequest.GetCodeList request = mock(CodeRequest.GetCodeList.class);
		CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);
		Page<CodeInfo.Main> codeList = new PageImpl<>(
			List.of(
				mock(CodeInfo.Main.class),
				mock(CodeInfo.Main.class)
			),
			mock(PageRequest.class),
			20
		);
		CodeResponse.CodeListResponse response = CodeResponse.CodeListResponse.builder()
			.codeInfos(codeList)
			.build();

		when(codeDtoMapper.of(request)).thenReturn(command);
		when(codeFacade.getCodes(any(), any())).thenReturn(codeList);
		when(codeDtoMapper.mapToCodeList(codeList)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/codes")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "updatedAt")
				.param("direction", "DESC"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codeInfos").exists())
			.andExpect(jsonPath("$.codeInfos.content").isArray())
			.andExpect(jsonPath("$.codeInfos.content.length()").value(2))
			.andDo(document("code/get-codes",
				resource(
					ResourceSnippetParameters.builder()
						.tag("code")
						.queryParameters(
							parameterWithName("page").description("조회할 페이지 번호"),
							parameterWithName("size").description("페이지 크기"),
							parameterWithName("sort").description("정렬 기준"),
							parameterWithName("direction").description("정렬 방향")
						)
						.responseFields(
							fieldWithPath("codeInfos.content").description("코드 목록"),
							fieldWithPath("codeInfos.content[].id").description("코드 ID"),
							fieldWithPath("codeInfos.content[].codeGroup").description("코드 그룹"),
							fieldWithPath("codeInfos.content[].messageId").description("메시지 ID"),
							fieldWithPath("codeInfos.content[].name").description("코드 이름"),
							fieldWithPath("codeInfos.content[].description").description("코드 설명"),
							fieldWithPath("codeInfos.content[].sortOrder").description("정렬 순서"),
							fieldWithPath("codeInfos.content[].isActive").description("활성화 여부"),
							fieldWithPath("codeInfos.content[].createMemberId").description("생성 멤버 ID"),
							fieldWithPath("codeInfos.content[].updateMemberId").description("수정 멤버 ID"),
							fieldWithPath("codeInfos.pageable").description("페이징 정보"),
							fieldWithPath("codeInfos.pageable.pageNumber").description("현재 페이지 번호"),
							fieldWithPath("codeInfos.pageable.pageSize").description("페이지 크기"),
							fieldWithPath("codeInfos.pageable.sort").description("정렬 정보"),
							fieldWithPath("codeInfos.pageable.offset").description("현재 페이지의 시작 위치"),
							fieldWithPath("codeInfos.pageable.paged").description("페이징 여부"),
							fieldWithPath("codeInfos.pageable.unpaged").description("페이징 미적용 여부"),
							fieldWithPath("codeInfos.totalPages").description("총 페이지 수"),
							fieldWithPath("codeInfos.totalElements").description("총 데이터 수"),
							fieldWithPath("codeInfos.size").description("현재 페이지 크기"),
							fieldWithPath("codeInfos.number").description("현재 페이지 번호"),
							fieldWithPath("codeInfos.sort").description("정렬 정보"),
							fieldWithPath("codeInfos.numberOfElements").description("현재 페이지의 데이터 수"),
							fieldWithPath("codeInfos.first").description("첫 번째 페이지 여부"),
							fieldWithPath("codeInfos.last").description("마지막 페이지 여부"),
							fieldWithPath("codeInfos.empty").description("페이지가 비어 있는지 여부")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("코드그룹 목록 조회 성공")
	void getGroupCodes() throws Exception {
		// given
		CodeRequest.GetCodeGroupList request = mock(CodeRequest.GetCodeGroupList.class);
		CodeCommand.GetCodeGroupList command = mock(CodeCommand.GetCodeGroupList.class);
		Page<CodeInfo.CodeGroupInfo> codeGroupList = new PageImpl<>(
			List.of(
				mock(CodeInfo.CodeGroupInfo.class),
				mock(CodeInfo.CodeGroupInfo.class)
			),
			mock(PageRequest.class),
			20
		);
		CodeResponse.CodeGroupListResponse response = CodeResponse.CodeGroupListResponse.builder()
			.codeInfos(codeGroupList)
			.build();

		when(codeDtoMapper.of(request)).thenReturn(command);
		when(codeFacade.getCodeGroups(any(), any())).thenReturn(codeGroupList);
		when(codeDtoMapper.mapToGroupList(codeGroupList)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/codes/code-group")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "updatedAt")
				.param("direction", "DESC"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.codeInfos").exists())
			.andExpect(jsonPath("$.codeInfos.content").isArray())
			.andExpect(jsonPath("$.codeInfos.content.length()").value(2))
			.andDo(document("code/get-group-codes",
				resource(
					ResourceSnippetParameters.builder()
						.tag("code")
						.queryParameters(
							parameterWithName("page").description("조회할 페이지 번호"),
							parameterWithName("size").description("페이지 크기"),
							parameterWithName("sort").description("정렬 기준"),
							parameterWithName("direction").description("정렬 방향")
						)
						.responseFields(
							fieldWithPath("codeInfos.content").description("코드 목록"),
							fieldWithPath("codeInfos.content[].id").description("코드 ID"),
							fieldWithPath("codeInfos.content[].name").description("코드그룹 이름"),
							fieldWithPath("codeInfos.content[].description").description("코드 설명"),
							fieldWithPath("codeInfos.content[].isActive").description("사용여부"),
							fieldWithPath("codeInfos.content[].createMemberId").description("생성 멤버 ID"),
							fieldWithPath("codeInfos.content[].updateMemberId").description("수정 멤버 ID"),
							fieldWithPath("codeInfos.pageable").description("페이징 정보"),
							fieldWithPath("codeInfos.pageable.pageNumber").description("현재 페이지 번호"),
							fieldWithPath("codeInfos.pageable.pageSize").description("페이지 크기"),
							fieldWithPath("codeInfos.pageable.sort").description("정렬 정보"),
							fieldWithPath("codeInfos.pageable.offset").description("현재 페이지의 시작 위치"),
							fieldWithPath("codeInfos.pageable.paged").description("페이징 여부"),
							fieldWithPath("codeInfos.pageable.unpaged").description("페이징 미적용 여부"),
							fieldWithPath("codeInfos.totalPages").description("총 페이지 수"),
							fieldWithPath("codeInfos.totalElements").description("총 데이터 수"),
							fieldWithPath("codeInfos.size").description("현재 페이지 크기"),
							fieldWithPath("codeInfos.number").description("현재 페이지 번호"),
							fieldWithPath("codeInfos.sort").description("정렬 정보"),
							fieldWithPath("codeInfos.numberOfElements").description("현재 페이지의 데이터 수"),
							fieldWithPath("codeInfos.first").description("첫 번째 페이지 여부"),
							fieldWithPath("codeInfos.last").description("마지막 페이지 여부"),
							fieldWithPath("codeInfos.empty").description("페이지가 비어 있는지 여부")
						)
						.build()
				)
			));
	}

	@Nested
	@DisplayName("코드 상세조회")
	class getCode {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("getCode: 성공")
			void getCode() throws Exception {
				// given
				String codeId = "code001";
				CodeInfo.Main code = mock(CodeInfo.Main.class);
				CodeResponse.CodeInfoResponse response = CodeResponse.CodeInfoResponse.builder()
					.codeInfo(code)
					.build();

				when(codeFacade.getCode(codeId)).thenReturn(code);
				when(codeDtoMapper.of(code)).thenReturn(response);

				// when & then
				mockMvc.perform(get("/api/codes/{id}", codeId))
					.andExpect(status().isOk())
					.andDo(document("code/get-code",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.responseFields(
									fieldWithPath("codeInfo.id").description("코드 ID"),
									fieldWithPath("codeInfo.codeGroup").description("코드그룹"),
									fieldWithPath("codeInfo.messageId").description("코드 ID"),
									fieldWithPath("codeInfo.name").description("코드그룹 이름"),
									fieldWithPath("codeInfo.description").description("코드 설명"),
									fieldWithPath("codeInfo.sortOrder").description("정렬순서"),
									fieldWithPath("codeInfo.isActive").description("사용여부"),
									fieldWithPath("codeInfo.createMemberId").description("생성 멤버 ID"),
									fieldWithPath("codeInfo.updateMemberId").description("수정 멤버 ID")
								)
								.build()
						)
					));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("getCode: 존재하지 않는 코드")
			void getCode_존재하지_않는_코드() throws Exception {
				// given
				String codeId = "code001";

				when(codeFacade.getCode(codeId))
					.thenThrow(new CustomException(ErrorCode.NOT_FOUND_CODE));

				// when & then
				mockMvc.perform(get("/api/codes/{id}", codeId))
					.andExpect(status().isNotFound())
					.andDo(document("code/get-code-fail",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("조회할 코드 ID")
								)
								.responseFields(
									fieldWithPath("status").description("HTTP 상태 코드"),
									fieldWithPath("error").description("HTTP 오류 메시지"),
									fieldWithPath("code").description("커스텀 에러 코드"),
									fieldWithPath("message").description("커스텀 에러 메시지")
								)
								.build()
						)
					));
			}
		}
	}

	@Nested
	@DisplayName("코드그룹 상세조회")
	class GetCodeGroup {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("getCodeGroup: 성공")
			void getCodeGroup() throws Exception {
				// given
				String codeGroupId = "invalidCode";
				CodeInfo.CodeGroupInfo code = mock(CodeInfo.CodeGroupInfo.class);
				CodeResponse.CodeGroupInfoResponse response = CodeResponse.CodeGroupInfoResponse.builder()
					.codeInfo(code)
					.build();

				when(codeFacade.getCodeGroup(codeGroupId)).thenReturn(code);
				when(codeDtoMapper.of(code)).thenReturn(response);

				// when & then
				mockMvc.perform(get("/api/codes/code-group/{id}", codeGroupId))
					.andExpect(status().isOk())
					.andDo(document("code/get-code-group",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("조회할 코드 ID")
								)
								.responseFields(
									fieldWithPath("codeInfo.id").description("코드 ID"),
									fieldWithPath("codeInfo.name").description("코드그룹 이름"),
									fieldWithPath("codeInfo.description").description("코드 설명"),
									fieldWithPath("codeInfo.isActive").description("사용여부"),
									fieldWithPath("codeInfo.createMemberId").description("생성 멤버 ID"),
									fieldWithPath("codeInfo.updateMemberId").description("수정 멤버 ID")
								)
								.build()
						)
					));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("getCodeGroup: 존재하지 않는 코드")
			void getCodeGroup_존재하지_않는_코드() throws Exception {
				// given
				String codeGroupId = "invalidCode";

				when(codeFacade.getCodeGroup(codeGroupId))
					.thenThrow(new CustomException(ErrorCode.NOT_FOUND_CODE));

				// when & then
				mockMvc.perform(get("/api/codes/code-group/{id}", codeGroupId))
					.andExpect(status().isNotFound())
					.andDo(document("code/get-code-group-fail",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("조회할 코드 ID")
								)
								.responseFields(
									fieldWithPath("status").description("HTTP 상태 코드"),
									fieldWithPath("error").description("HTTP 오류 메시지"),
									fieldWithPath("code").description("커스텀 에러 코드"),
									fieldWithPath("message").description("커스텀 에러 메시지")
								)
								.build()
						)
					));
			}
		}
	}

	@Nested
	@DisplayName("코드 그룹별 코드 목록 조회")
	class GetCodesByCodeGroup {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("GetCodesByCodeGroup: 성공")
			void getCodesByCodeGroup() throws Exception {
				// given
				String codeGroupId = "group1";
				CodeRequest.GetCodeList request = mock(CodeRequest.GetCodeList.class);
				CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);
				Page<CodeInfo.Main> codeList = new PageImpl<>(
					List.of(
						mock(CodeInfo.Main.class),
						mock(CodeInfo.Main.class)
					),
					mock(PageRequest.class),
					20
				);
				CodeResponse.CodeListResponse response = CodeResponse.CodeListResponse.builder()
					.codeInfos(codeList)
					.build();

				when(codeDtoMapper.of(request)).thenReturn(command);
				when(codeFacade.getCodesByCodeGroup(any(), any(), any())).thenReturn(codeList);
				when(codeDtoMapper.mapToCodeList(codeList)).thenReturn(response);

				// when & then
				mockMvc.perform(get("/api/codes/code-group/{codeGroupId}/codes", codeGroupId)
						.param("page", "0")
						.param("size", "10")
						.param("sort", "updatedAt")
						.param("direction", "DESC"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.codeInfos").exists())
					.andExpect(jsonPath("$.codeInfos.content").isArray())
					.andExpect(jsonPath("$.codeInfos.content.length()").value(2))
					.andDo(document("code/get-codes-by-code-group",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("codeGroupId").description("조회할 코드 그룹 ID")
								)
								.queryParameters(
									parameterWithName("page").description("조회할 페이지 번호"),
									parameterWithName("size").description("페이지 크기"),
									parameterWithName("sort").description("정렬 기준"),
									parameterWithName("direction").description("정렬 방향")
								)
								.responseFields(
									fieldWithPath("codeInfos.content").description("코드 목록"),
									fieldWithPath("codeInfos.content[].id").description("코드 ID"),
									fieldWithPath("codeInfos.content[].codeGroup").description("코드 그룹"),
									fieldWithPath("codeInfos.content[].messageId").description("메시지 ID"),
									fieldWithPath("codeInfos.content[].name").description("코드 이름"),
									fieldWithPath("codeInfos.content[].description").description("코드 설명"),
									fieldWithPath("codeInfos.content[].sortOrder").description("정렬 순서"),
									fieldWithPath("codeInfos.content[].isActive").description("활성화 여부"),
									fieldWithPath("codeInfos.content[].createMemberId").description("생성 멤버 ID"),
									fieldWithPath("codeInfos.content[].updateMemberId").description("수정 멤버 ID"),
									fieldWithPath("codeInfos.pageable").description("페이징 정보"),
									fieldWithPath("codeInfos.pageable.pageNumber").description("현재 페이지 번호"),
									fieldWithPath("codeInfos.pageable.pageSize").description("페이지 크기"),
									fieldWithPath("codeInfos.pageable.sort").description("정렬 정보"),
									fieldWithPath("codeInfos.pageable.offset").description("현재 페이지의 시작 위치"),
									fieldWithPath("codeInfos.pageable.paged").description("페이징 여부"),
									fieldWithPath("codeInfos.pageable.unpaged").description("페이징 미적용 여부"),
									fieldWithPath("codeInfos.totalPages").description("총 페이지 수"),
									fieldWithPath("codeInfos.totalElements").description("총 데이터 수"),
									fieldWithPath("codeInfos.size").description("현재 페이지 크기"),
									fieldWithPath("codeInfos.number").description("현재 페이지 번호"),
									fieldWithPath("codeInfos.sort").description("정렬 정보"),
									fieldWithPath("codeInfos.numberOfElements").description("현재 페이지의 데이터 수"),
									fieldWithPath("codeInfos.first").description("첫 번째 페이지 여부"),
									fieldWithPath("codeInfos.last").description("마지막 페이지 여부"),
									fieldWithPath("codeInfos.empty").description("페이지가 비어 있는지 여부")
								)
								.build()
						)
					));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("GetCodesByCodeGroup: 그룹 없음")
			void getCodesByCodeGroup_그룹_없음() throws Exception {
				// given
				String codeGroupId = "invalidGroup";
				CodeRequest.GetCodeList request = mock(CodeRequest.GetCodeList.class);
				CodeCommand.GetCodeList command = mock(CodeCommand.GetCodeList.class);

				when(codeDtoMapper.of(request)).thenReturn(command);
				when(codeFacade.getCodesByCodeGroup(any(), any(), any()))
					.thenThrow(new CustomException(ErrorCode.NOT_FOUND_CODE_GROUP));

				// when & then
				mockMvc.perform(get("/api/codes/code-group/{codeGroupId}/codes", codeGroupId))
					.andExpect(status().isNotFound())
					.andDo(document("code/get-codes-by-code-group-fail",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("codeGroupId").description("조회할 코드 그룹 ID")
								)
								.responseFields(
									fieldWithPath("status").description("HTTP 상태 코드"),
									fieldWithPath("error").description("HTTP 오류 메시지"),
									fieldWithPath("code").description("커스텀 에러 코드"),
									fieldWithPath("message").description("커스텀 에러 메시지")
								)
								.build()
						)
					));
			}
		}
	}

	@Test
	@DisplayName("코드 생성 성공")
	void createCode() throws Exception {
		// given
		String codeGroupId = "group1";
		CodeRequest.CreateCode request = new CodeRequest.CreateCode();
		CodeCommand.CreateCode command = mock(CodeCommand.CreateCode.class);

		when(codeDtoMapper.of(request)).thenReturn(command);
		doNothing().when(codeFacade).createCode(command, codeGroupId);

		// when & then
		mockMvc.perform(post("/api/codes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(document("code/create-code",
				resource(
					ResourceSnippetParameters.builder()
						.tag("code")
						.requestFields(
							fieldWithPath("id").description("코드 ID"),
							fieldWithPath("groupId").description("코드그룹 ID"),
							fieldWithPath("messageId").description("메시지 ID"),
							fieldWithPath("name").description("코드명"),
							fieldWithPath("description").description("코드설명"),
							fieldWithPath("sortOrder").description("정렬순서"),
							fieldWithPath("isActive").description("사용여부")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("코드그룹 생성 성공")
	void createCodeGroup() throws Exception {
		// given
		CodeRequest.CreateCodeGroup request = new CodeRequest.CreateCodeGroup();
		CodeCommand.CreateCodeGroup command = mock(CodeCommand.CreateCodeGroup.class);

		when(codeDtoMapper.of(request)).thenReturn(command);
		doNothing().when(codeFacade).createCodeGroup(command);

		// when & then
		mockMvc.perform(post("/api/codes/code-group")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(document("code/create-code-group",
				resource(
					ResourceSnippetParameters.builder()
						.tag("code")
						.requestFields(
							fieldWithPath("id").description("코드 ID"),
							fieldWithPath("name").description("코드명"),
							fieldWithPath("description").description("코드설명"),
							//fieldWithPath("sortOrder").description("정렬순서"),
							fieldWithPath("isActive").description("사용여부")
						)
						.build()
				)
			));
	}

	@Nested
	@DisplayName("코드 업데이트")
	class UpdateCode {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {
			@Test
			@DisplayName("updateCode: 성공")
			void updateCode() throws Exception {
				// given
				String codeId = "code001";
				CodeRequest.UpdateCode request = new CodeRequest.UpdateCode();
				CodeCommand.UpdateCode command = mock(CodeCommand.UpdateCode.class);

				when(codeDtoMapper.of(request)).thenReturn(command);
				doNothing().when(codeFacade).updateCode(command, codeId);

				// when & then
				mockMvc.perform(put("/api/codes/{id}", codeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(content().string("true"))
					.andDo(document("code/update-code",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("업데이트할 코드 ID")
								)
								.requestFields(
									fieldWithPath("name").description("코드명"),
									fieldWithPath("description").description("코드설명"),
									fieldWithPath("sortOrder").description("정렬순서"),
									fieldWithPath("isActive").description("사용여부")
								)
								.build()
						)
					));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("updateCode: 코드 없음")
			void updateCode_코드_없음() throws Exception {
				// given
				String codeId = "invalidCode";
				CodeRequest.UpdateCode request = new CodeRequest.UpdateCode();
				CodeCommand.UpdateCode command = mock(CodeCommand.UpdateCode.class);

				when(codeDtoMapper.of(request)).thenReturn(command);
				doThrow(new CustomException(ErrorCode.NOT_FOUND_CODE))
					.when(codeFacade).updateCode(any(), any());

				// when & then
				mockMvc.perform(put("/api/codes/{id}", codeId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isNotFound())
					.andDo(document("code/update-code-fail",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("조회할 코드 ID")
								)
								.responseFields(
									fieldWithPath("status").description("HTTP 상태 코드"),
									fieldWithPath("error").description("HTTP 오류 메시지"),
									fieldWithPath("code").description("커스텀 에러 코드"),
									fieldWithPath("message").description("커스텀 에러 메시지")
								)
								.build()
						)
					));
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
			void updateCodeGroup() throws Exception {
				// given
				String codeGroupId = "group001";
				CodeRequest.UpdateCodeGroup request = new CodeRequest.UpdateCodeGroup();
				CodeCommand.UpdateCodeGroup command = mock(CodeCommand.UpdateCodeGroup.class);

				when(codeDtoMapper.of(request)).thenReturn(command);
				doNothing().when(codeFacade).updateCodeGroup(command, codeGroupId);

				// when & then
				mockMvc.perform(put("/api/codes/code-group/{id}", codeGroupId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(content().string("true"))
					.andDo(document("code/update-code-group",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("조회할 코드 ID")
								)
								.requestFields(
									fieldWithPath("name").description("코드명"),
									fieldWithPath("description").description("코드설명"),
									fieldWithPath("isActive").description("사용여부")
								)
								.build()
						)
					));
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {
			@Test
			@DisplayName("updateCodeGroup: 코드 없음")
			void updateCodeGroup_코드_없음() throws Exception {
				// given
				String codeGroupId = "invalidCode";
				CodeRequest.UpdateCodeGroup request = new CodeRequest.UpdateCodeGroup();
				CodeCommand.UpdateCodeGroup command = mock(CodeCommand.UpdateCodeGroup.class);

				when(codeDtoMapper.of(request)).thenReturn(command);
				doThrow(new CustomException(ErrorCode.NOT_FOUND_CODE))
					.when(codeFacade).updateCodeGroup(any(), any());

				// when & then
				mockMvc.perform(put("/api/codes/code-group/{id}", codeGroupId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isNotFound())
					.andDo(document("code/update-code-group-fail",
						resource(
							ResourceSnippetParameters.builder()
								.tag("code")
								.pathParameters(
									parameterWithName("id").description("조회할 코드 ID")
								)
								.responseFields(
									fieldWithPath("status").description("HTTP 상태 코드"),
									fieldWithPath("error").description("HTTP 오류 메시지"),
									fieldWithPath("code").description("커스텀 에러 코드"),
									fieldWithPath("message").description("커스텀 에러 메시지")
								)
								.build()
						)
					));
			}
		}
	}

	@Test
	@DisplayName("코드 삭제 성공")
	void deleteCode() throws Exception {
		// given
		String codeId = "code001";
		;
		doNothing().when(codeFacade).deleteCode(codeId);

		// when & then
		mockMvc.perform(delete("/api/codes/{id}", codeId))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(document("code/delete-code",
				resource(
					ResourceSnippetParameters.builder()
						.tag("code")
						.pathParameters(
							parameterWithName("id").description("조회할 코드 ID")
						)
						.build()
				)
			));
	}
}
