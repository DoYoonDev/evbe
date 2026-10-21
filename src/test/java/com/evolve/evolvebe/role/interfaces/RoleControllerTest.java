package com.evolve.evolvebe.role.interfaces;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.evolve.evolvebe.role.application.RoleFacade;
import com.evolve.evolvebe.role.domain.RoleCommand;
import com.evolve.evolvebe.role.domain.RoleInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = RoleController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = {SecurityConfig.class, JwtFilter.class}
		)
	}
)
@AutoConfigureRestDocs
class RoleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockitoBean
	private RoleFacade roleFacade;

	@MockitoBean
	private RoleDtoMapper roleDtoMapper;

	@Test
	@DisplayName("역할 목록 조회 성공")
	void getRoles() throws Exception {
		// given
		List<RoleInfo.Main> roles = List.of(
			mock(RoleInfo.Main.class),
			mock(RoleInfo.Main.class)
		);
		RoleResponse.RoleListResponse response = RoleResponse.RoleListResponse.builder()
			.roleInfos(roles)
			.build();

		when(roleFacade.getRoles()).thenReturn(roles);
		when(roleDtoMapper.mapToRoleList(roles)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/roles"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roleInfos").exists())
			.andExpect(jsonPath("$.roleInfos").isArray())
			.andExpect(jsonPath("$.roleInfos.length()").value(2))
			.andDo(document("role/get-roles",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.responseFields(
							fieldWithPath("roleInfos").description("역할 목록"),
							fieldWithPath("roleInfos[].id").description("역할 ID"),
							fieldWithPath("roleInfos[].name").description("역할 이름"),
							fieldWithPath("roleInfos[].isActive").description("활성화 여부"),
                            fieldWithPath("roleInfos[].level").description("역할 레벨"),
							fieldWithPath("roleInfos[].description").description("역할 설명")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("역할 조회 성공")
	void getRole() throws Exception {
		// given
		String roleId = "TestRole";
		RoleInfo.Main role = mock(RoleInfo.Main.class);
		RoleResponse.RoleInfoResponse response = RoleResponse.RoleInfoResponse.builder()
			.roleInfo(role)
			.build();

		when(roleFacade.getRole(roleId)).thenReturn(role);
		when(roleDtoMapper.from(role)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/roles/{id}", roleId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roleInfo").exists())
			.andDo(document("role/get-role",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.pathParameters(
							parameterWithName("id").description("역할 ID")
						)
						.responseFields(
							fieldWithPath("roleInfo").description("역할 목록"),
							fieldWithPath("roleInfo.id").description("역할 ID"),
							fieldWithPath("roleInfo.name").description("역할 이름"),
							fieldWithPath("roleInfo.isActive").description("활성화 여부"),
                            fieldWithPath("roleInfo.level").description("역할 레벨"),
							fieldWithPath("roleInfo.description").description("역할 설명")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("역할별 메뉴 목록 조회 성공")
	void getMenusByRole() throws Exception {
		// given
		String roleId = "TestRole";
		List<RoleInfo.Menu> roles = List.of(
			mock(RoleInfo.Menu.class),
			mock(RoleInfo.Menu.class)
		);
		RoleResponse.MenuListResponse response = RoleResponse.MenuListResponse.builder()
			.roleMenuInfos(roles)
			.build();

		when(roleFacade.getMenusByRoleId(roleId)).thenReturn(roles);
		when(roleDtoMapper.mapToMenuList(roles)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/roles/{id}/menus", roleId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roleMenuInfos").exists())
			.andExpect(jsonPath("$.roleMenuInfos").isArray())
			.andExpect(jsonPath("$.roleMenuInfos.length()").value(2))
			.andDo(document("role/get-roles",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.responseFields(
							fieldWithPath("roleMenuInfos").description("역할 목록"),
							fieldWithPath("roleMenuInfos[].menu").description("메뉴 정보"),
							fieldWithPath("roleMenuInfos[].isChecked").description("체크 여부")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("역할별 멤버 목록 조회 성공")
	void getMembersByRole() throws Exception {
		// given
		String roleId = "TestRole";
		List<RoleInfo.Member> roles = List.of(
			mock(RoleInfo.Member.class),
			mock(RoleInfo.Member.class)
		);
		RoleResponse.MemberListResponse response = RoleResponse.MemberListResponse.builder()
			.roleMemberInfos(roles)
			.build();

		when(roleFacade.getMembersByRoleId(roleId)).thenReturn(roles);
		when(roleDtoMapper.mapToMemberList(roles)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/roles/{id}/members", roleId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.roleMemberInfos").exists())
			.andExpect(jsonPath("$.roleMemberInfos").isArray())
			.andExpect(jsonPath("$.roleMemberInfos.length()").value(2))
			.andDo(document("role/get-roles",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.responseFields(
							fieldWithPath("roleMemberInfos").description("역할 목록"),
							fieldWithPath("roleMemberInfos[].memberId").description("유저 ID"),
							fieldWithPath("roleMemberInfos[].koName").description("유저 한글명"),
							fieldWithPath("roleMemberInfos[].enName").description("유저 영어명")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("역할 생성")
	void createRole() throws Exception {
		// given
		RoleRequest.CreateRole request = mock(RoleRequest.CreateRole.class);
		RoleCommand.CreateRole command = mock(RoleCommand.CreateRole.class);

		when(roleDtoMapper.from(request)).thenReturn(command);
		doNothing().when(roleFacade).createRole(command);

		// when & then
		mockMvc.perform(post("/api/roles")
				.contentType("application/json")
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(document("role/create-role",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.requestFields(
							fieldWithPath("id").description("역할 ID"),
							fieldWithPath("name").description("역할 이름"),
							fieldWithPath("isActive").description("활성화 여부"),
                            fieldWithPath("level").description("역할 레벨"),
							fieldWithPath("description").description("역할 설명")
						)
						.build()
				)
			));
	}

	@Nested
	@DisplayName("역할 수정")
	class UpdateRole {
		@Nested
		@DisplayName("역할 수정 성공")
		class SuccessCase {
			@Test
			@DisplayName("updateRole: 성공")
			void updateRole() throws Exception {
				// given
				String roleId = "TestRole";
				RoleRequest.UpdateRole request = new RoleRequest.UpdateRole();
				RoleCommand.UpdateRole command = mock(RoleCommand.UpdateRole.class);

				when(roleDtoMapper.from(request)).thenReturn(command);
				doNothing().when(roleFacade).updateRole(command, roleId);

				// when & then
				mockMvc.perform(put("/api/roles/{id}", roleId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(content().string("true"))
					.andDo(document("role/update-role",
						resource(
							ResourceSnippetParameters.builder()
								.tag("role")
								.pathParameters(
									parameterWithName("id").description("역할 ID")
								)
								.requestFields(
									fieldWithPath("name").description("역할 이름"),
									fieldWithPath("isActive").description("활성화 여부"),
                                    fieldWithPath("level").description("역할 레벨"),
									fieldWithPath("description").description("역할 설명")
								)
								.build()
						)
					));
			}
		}

		@Nested
		@DisplayName("역할 수정 실패")
		class FailCase {
			@Test
			@DisplayName("updateRole: 역할없음")
			void updateRole_역할없음() throws Exception {
				// given
				String roleId = "TestRole";
				RoleRequest.UpdateRole request = new RoleRequest.UpdateRole();
				RoleCommand.UpdateRole command = mock(RoleCommand.UpdateRole.class);

				when(roleDtoMapper.from(request)).thenReturn(command);
				doThrow(new CustomException(ErrorCode.NOT_FOUND_ROLE))
					.when(roleFacade).updateRole(any(), any());

				// when & then
				mockMvc.perform(put("/api/roles/{id}", roleId)
						.contentType("application/json")
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isNotFound());
			}
		}
	}

	@Test
	@DisplayName("역할 삭제")
	void deleteRole() throws Exception {
		// given
		String roleId = "TestRole";

		doNothing().when(roleFacade).deleteRole(roleId);

		// when & then
		mockMvc.perform(delete("/api/roles/{id}", roleId))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(document("role/delete-role",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.pathParameters(
							parameterWithName("id").description("역할 ID")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("역할 내 멤버추가")
	void addMemberToRole() throws Exception {
		// given
		String roleId = "TestRole";
		String memberId = "TestMember";

		RoleCommand.AddMember command = mock(RoleCommand.AddMember.class);

		when(roleDtoMapper.mapToAddCommand(roleId, memberId)).thenReturn(command);
		doNothing().when(roleFacade).addMemberToRole(command);

		// when & then
		mockMvc.perform(post("/api/roles/{id}/members/{memberId}", roleId, memberId))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(document("role/add-member-to-role",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.pathParameters(
							parameterWithName("id").description("역할 ID"),
							parameterWithName("memberId").description("멤버 ID")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("역할 내 멤버삭제")
	void deleteMemberFromRole() throws Exception {
		// given
		String roleId = "TestRole";
		String memberId = "TestMember";

		RoleCommand.DeleteMember command = mock(RoleCommand.DeleteMember.class);

		when(roleDtoMapper.mapToDeleteCommand(roleId, memberId)).thenReturn(command);
		doNothing().when(roleFacade).deleteMemberFromRole(command);

		// when & then
		mockMvc.perform(delete("/api/roles/{id}/members/{memberId}", roleId, memberId))
			.andExpect(status().isOk())
			.andExpect(content().string("true"))
			.andDo(document("role/delete-member-from-role",
				resource(
					ResourceSnippetParameters.builder()
						.tag("role")
						.pathParameters(
							parameterWithName("id").description("역할 ID"),
							parameterWithName("memberId").description("멤버 ID")
						)
						.build()
				)
			));
	}
}