package com.evolve.evolvebe.menu.interfaces;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.evolve.evolvebe.menu.application.MenuFacade;
import com.evolve.evolvebe.menu.domain.MenuCommand;
import com.evolve.evolvebe.menu.domain.MenuInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
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

import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
	controllers = MenuController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = {SecurityConfig.class, JwtFilter.class}
		)
	}
)
@AutoConfigureRestDocs
class MenuControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockitoBean
	private MenuFacade menuFacade;

	@MockitoBean
	private MenuDtoMapper menuDtoMapper;

	@Test
	@DisplayName("메뉴 ID로 조회")
	void getMenu() throws Exception {
		// given
		Integer id = 1;
		MenuInfo.Main menu = mock(MenuInfo.Main.class);
		MenuResponse.Main response = MenuResponse.Main.builder().menuInfo(menu).build();

		when(menuFacade.getMenu(id)).thenReturn(menu);
		when(menuDtoMapper.from(menu)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/menus/{id}", id))
			.andExpect(status().isOk())
			.andDo(document("menu/get-menu",
				resource(
					ResourceSnippetParameters.builder()
						.tag("menu")
						.responseFields(
							fieldWithPath("menuInfo.id").description("메뉴 ID"),
							fieldWithPath("menuInfo.parentMenuId").description("싱위메뉴 ID"),
							fieldWithPath("menuInfo.messageId").description("메시지 ID"),
							fieldWithPath("menuInfo.url").description("메뉴 url"),
							fieldWithPath("menuInfo.name").description("메뉴 이름"),
							fieldWithPath("menuInfo.isActive").description("생성여부"),
							fieldWithPath("menuInfo.isDelete").description("삭제여부"),
							fieldWithPath("menuInfo.sortOrder").description("정렬"),
							fieldWithPath("menuInfo.description").description("설명"),
							fieldWithPath("menuInfo.level").description("메뉴 레벨"),
							fieldWithPath("menuInfo.createMemberId").description("생성 멤버 ID"),
							fieldWithPath("menuInfo.updateMemberId").description("수정 멤버 ID")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("메뉴 목록 조회")
	void getMenus() throws Exception {
		// given
		Boolean isActive = Boolean.TRUE;
		List<MenuInfo.Main> menus = List.of(mock(MenuInfo.Main.class));
		MenuResponse.MenuList response = MenuResponse.MenuList.builder().menuInfos(menus).build();

		when(menuFacade.getMenus(isActive)).thenReturn(menus);
		when(menuDtoMapper.from(menus)).thenReturn(response);

		// when & then
		mockMvc.perform(get("/api/menus").param("isActive", String.valueOf(isActive)))
			.andExpect(status().isOk())
			.andDo(document("menu/get-menus",
				resource(
					ResourceSnippetParameters.builder()
						.tag("menu")
						.responseFields(
							fieldWithPath("menuInfos").description("메뉴 정보"),
							fieldWithPath("menuInfos[].id").description("메뉴 ID"),
							fieldWithPath("menuInfos[].parentMenuId").description("싱위메뉴 ID"),
							fieldWithPath("menuInfos[].messageId").description("메시지 ID"),
							fieldWithPath("menuInfos[].url").description("메뉴 url"),
							fieldWithPath("menuInfos[].name").description("메뉴 이름"),
							fieldWithPath("menuInfos[].isActive").description("생성여부"),
							fieldWithPath("menuInfos[].isDelete").description("삭제여부"),
							fieldWithPath("menuInfos[].sortOrder").description("정렬"),
							fieldWithPath("menuInfos[].description").description("설명"),
							fieldWithPath("menuInfos[].level").description("메뉴 레벨"),
							fieldWithPath("menuInfos[].createMemberId").description("생성 멤버 ID"),
							fieldWithPath("menuInfos[].updateMemberId").description("수정 멤버 ID")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("메뉴 생성")
	void createMenu() throws Exception {
		// given
		MenuRequest.Create request = MenuRequest.Create.builder()
			.messageId("message.id")
			.url("url")
			.name("name")
			.level(1)
			.build();
		MenuCommand.Create command = MenuCommand.Create.builder()
			.messageId("message.id")
			.url("url")
			.name("name")
			.level(1)
			.build();
		MenuInfo.Main menu = MenuInfo.Main.builder()
			.messageId("message.id")
			.url("url")
			.name("name")
			.level(1)
			.build();
		MenuResponse.Main response = MenuResponse.Main.builder().menuInfo(menu).build();

		when(menuDtoMapper.from(any(MenuRequest.Create.class))).thenReturn(command);
		// when(menuDtoMapper.from(eq(request))).thenReturn(command);
		when(menuFacade.createMenu(command)).thenReturn(menu);
		when(menuDtoMapper.from(menu)).thenReturn(response);

		// when & then
		mockMvc.perform(post("/api/menus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andDo(document("menu/create-menu",
				resource(
					ResourceSnippetParameters.builder()
						.tag("menu")
						.requestFields(
							fieldWithPath("parentMenuId").description("상위메뉴 ID"),
							fieldWithPath("messageId").description("메시지 ID"),
							fieldWithPath("url").description("메뉴 url"),
							fieldWithPath("name").description("메뉴 이름"),
							fieldWithPath("isActive").description("생성여부"),
							fieldWithPath("isDelete").description("삭제여부"),
							fieldWithPath("sortOrder").description("정렬"),
							fieldWithPath("description").description("설명"),
							fieldWithPath("level").description("메뉴 레벨")
						)
						.responseFields(
							fieldWithPath("menuInfo.id").description("메뉴 ID"),
							fieldWithPath("menuInfo.parentMenuId").description("싱위메뉴 ID"),
							fieldWithPath("menuInfo.messageId").description("메시지 ID"),
							fieldWithPath("menuInfo.url").description("메뉴 url"),
							fieldWithPath("menuInfo.name").description("메뉴 이름"),
							fieldWithPath("menuInfo.isActive").description("생성여부"),
							fieldWithPath("menuInfo.isDelete").description("삭제여부"),
							fieldWithPath("menuInfo.sortOrder").description("정렬"),
							fieldWithPath("menuInfo.description").description("설명"),
							fieldWithPath("menuInfo.level").description("메뉴 레벨"),
							fieldWithPath("menuInfo.createMemberId").description("생성 멤버 ID"),
							fieldWithPath("menuInfo.updateMemberId").description("수정 멤버 ID")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("메뉴 수정")
	void updateMenu() throws Exception {
		// given
		Integer id = 1;
		MenuRequest.Update request = mock(MenuRequest.Update.class);
		MenuCommand.Update command = mock(MenuCommand.Update.class);

		when(menuDtoMapper.from(request)).thenReturn(command);
		doNothing().when(menuFacade).updateMenu(id, command);

		// when & then
		mockMvc.perform(put("/api/menus/{id}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(Boolean.TRUE))
			.andDo(document("menu/update-menu",
				resource(
					ResourceSnippetParameters.builder()
						.tag("menu")
						.pathParameters(
							parameterWithName("id").description("메뉴 ID")
						)
						.requestFields(
							fieldWithPath("messageId").description("메시지 ID"),
							fieldWithPath("url").description("메뉴 url"),
							fieldWithPath("name").description("메뉴 이름"),
							fieldWithPath("isActive").description("생성여부"),
							fieldWithPath("description").description("설명")
						)
						.build()
				)

			));
	}

	@Test
	@DisplayName("메뉴 순서 수정")
	void updateMenuPosition() throws Exception {
		// given
		MenuRequest.UpdatePosition request = mock(MenuRequest.UpdatePosition.class);
		MenuCommand.UpdatePosition command = mock(MenuCommand.UpdatePosition.class);

		when(menuDtoMapper.from(request)).thenReturn(command);
		doNothing().when(menuFacade).updateMenuPositions(command);

		// when & then
		mockMvc.perform(put("/api/menus/position")
				.contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(Boolean.TRUE))
			.andDo(document("menu/update-menu-position",
				resource(
					ResourceSnippetParameters.builder()
						.tag("menu")
						.requestFields(
							fieldWithPath("level1").description("1레벨 메뉴 ID 리스트"),
							fieldWithPath("level2").description("2레벨 메뉴 ID 리스트"),
							fieldWithPath("level3").description("3레벨 메뉴 ID 리스트")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("메뉴 삭제")
	void deleteMenu() throws Exception {
		// given
		Integer id = 1;
		doNothing().when(menuFacade).deleteMenu(id);

		// when & then
		mockMvc.perform(delete("/api/menus/{id}", id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").value(Boolean.TRUE))
			.andDo(document("menu/delete-menu",
				resource(
					ResourceSnippetParameters.builder()
						.tag("menu")
						.pathParameters(
							parameterWithName("id").description("메뉴 ID")
						)
						.build()
				)
			));
	}
}