package com.evolve.evolvebe.log.interfaces;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.evolve.evolvebe.log.application.LogFacade;
import com.evolve.evolvebe.log.domain.LogCommand;
import com.evolve.evolvebe.log.domain.LogInfo;

import com.evolve.evolvebe.member.domain.MemberRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
	controllers = LogController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = {SecurityConfig.class, JwtFilter.class})
	}
)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class LogControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockitoBean
	private LogFacade logFacade;

	@MockitoBean
	private LogDtoMapper logDtoMapper;

	@MockitoBean
	private MemberRepository memberRepository;

	@Test
	@DisplayName("로그 접근 목록 조회")
	void getLogAccess() throws Exception {
		// given
		LogRequest.GetLogAccessList request = mock(LogRequest.GetLogAccessList.class);
		LogCommand.GetLogAccessList command = mock(LogCommand.GetLogAccessList.class);
		Page<LogInfo.LogAccess> logAccessPage = new PageImpl<>(
			List.of(
				mock(LogInfo.LogAccess.class),
				mock(LogInfo.LogAccess.class)
			),
			mock(PageRequest.class),
			20
		);
		LogResponse.LogAccessPage response = LogResponse.LogAccessPage.builder()
			.logAccessInfos(logAccessPage)
			.build();
//		LogResponse.LogAccessPage response = mock(LogResponse.LogAccessPage.class);

		when(logFacade.getLogAccess(any(), any())).thenReturn(logAccessPage);
		when(logDtoMapper.mapToLogAccessList(logAccessPage)).thenReturn(response);
//		when(LogResponse.LogAccessPage.from(logAccessPage)).thenReturn(response);
//		LogResponse.LogAccessPage response = LogResponse.LogAccessPage.from(logAccessPage);

		// when & then
//		mockMvc.perform(get("/api/logs/accesss")
//				.param("page", "0")
//				.param("size", "10")
//				.param("sort", "createdAt")
//				.param("direction", "DESC")
//			)
//			.andExpect(status().isOk())
////			.andExpect(jsonPath("$.logAccessInfos").exists())
//			.andExpect(jsonPath("$.logAccessInfos").isArray())
//			.andExpect(jsonPath("$.logAccessInfos.length()").value(2))
//			.andDo(document("log/get-log-accesss",
//				resource(
//					ResourceSnippetParameters.builder()
//						.tag("logAccess")
//						.responseFields(
//							fieldWithPath("logAccessInfos").description("메시지 정보"),
//							fieldWithPath("logAccessInfos.content").description("메시지 목록"),
//							fieldWithPath("logAccessInfos.content[].id").description("접근로그 ID"),
//							fieldWithPath("logAccessInfos.content[].member").description("회원"),
//							fieldWithPath("logAccessInfos.content[].requestUrl").description("요청 URL"),
//							fieldWithPath("logAccessInfos.content[].requestMethod").description("요청 메서드"),
//							fieldWithPath("logAccessInfos.content[].requestPayload").description("요청 페이로드"),
//							fieldWithPath("logAccessInfos.content[].requestIp").description("요청 IP"),
//							fieldWithPath("logAccessInfos.content[].responseStatus").description("응답 상태"),
//							fieldWithPath("logAccessInfos.content[].responseBody").description("응답 바디"),
//							fieldWithPath("logAccessInfos.content[].createdAt").description("생성 일자"),
//							fieldWithPath("logAccessInfos.pageable").description("페이지네이션 정보"),
//							fieldWithPath("logAccessInfos.pageable.pageNumber").description("현재 페이지 번호"),
//							fieldWithPath("logAccessInfos.pageable.pageSize").description("페이지 크기"),
//							fieldWithPath("logAccessInfos.pageable.sort").description("정렬 정보"),
//							fieldWithPath("logAccessInfos.pageable.offset").description("현재 페이지의 오프셋"),
//							fieldWithPath("logAccessInfos.pageable.unpaged").description("페이지네이션이 비활성화되었는지 여부"),
//							fieldWithPath("logAccessInfos.pageable.paged").description("페이지네이션이 활성화되었는지 여부"),
//							fieldWithPath("logAccessInfos.last").description("현재 페이지가 마지막 페이지인지 여부"),
//							fieldWithPath("logAccessInfos.totalPages").description("전체 페이지 수"),
//							fieldWithPath("logAccessInfos.totalElements").description("전체 요소 수"),
//							fieldWithPath("logAccessInfos.size").description("페이지당 요소 수"),
//							fieldWithPath("logAccessInfos.number").description("현재 페이지 번호"),
//							fieldWithPath("logAccessInfos.sort").description("페이지의 정렬 정보"),
//							fieldWithPath("logAccessInfos.numberOfElements").description("현재 페이지의 요소 수"),
//							fieldWithPath("logAccessInfos.first").description("현재 페이지가 첫 번째 페이지인지 여부"),
//							fieldWithPath("logAccessInfos.empty").description("현재 페이지가 비어 있는지 여부")
//						)
//						.build()
//				)
//		));
		mockMvc.perform(get("/api/logs/accesss")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "createdAt")
				.param("direction", "DESC")
			)
			.andExpect(status().isOk())
			// logAccessInfos 자체가 배열이므로 content 제거
			.andExpect(jsonPath("$.logAccessInfos").exists())
			.andExpect(jsonPath("$.logAccessInfos.content").isArray())
			.andExpect(jsonPath("$.logAccessInfos.content.length()").value(2))
			// 페이지 정보 검증
//			.andExpect(jsonPath("$.page").value(0))
//			.andExpect(jsonPath("$.size").value(10))
//			.andExpect(jsonPath("$.totalElements").value(2))
//			.andExpect(jsonPath("$.totalPages").value(1))
			.andDo(document("log/get-log-accesss",
				resource(
					ResourceSnippetParameters.builder()
						.tag("logAccess")
						.responseFields(
								fieldWithPath("logAccessInfos.content").description("접근 로그 목록"),
								fieldWithPath("logAccessInfos.content[].id").description("접근로그 ID"),
								fieldWithPath("logAccessInfos.content[].memberId").description("회원"),
								fieldWithPath("logAccessInfos.content[].requestUrl").description("요청 URL"),
								fieldWithPath("logAccessInfos.content[].requestMethod").description("요청 메서드"),
								fieldWithPath("logAccessInfos.content[].requestPayload").description("요청 페이로드"),
								fieldWithPath("logAccessInfos.content[].requestIp").description("요청 IP"),
								fieldWithPath("logAccessInfos.content[].responseStatus").description("응답 상태"),
								fieldWithPath("logAccessInfos.content[].responseBody").description("응답 바디"),
								fieldWithPath("logAccessInfos.content[].createdAt").description("생성 일자"),

								fieldWithPath("logAccessInfos.pageable").description("페이징 정보"),
								fieldWithPath("logAccessInfos.pageable.pageNumber").description("현재 페이지 번호"),
								fieldWithPath("logAccessInfos.pageable.pageSize").description("페이지 크기"),
								fieldWithPath("logAccessInfos.pageable.offset").description("오프셋"),
								fieldWithPath("logAccessInfos.pageable.paged").description("페이징 여부"),
								fieldWithPath("logAccessInfos.pageable.unpaged").description("페이징 미적용 여부"),

								fieldWithPath("logAccessInfos.totalPages").description("총 페이지 수"),
								fieldWithPath("logAccessInfos.totalElements").description("총 데이터 수"),
								fieldWithPath("logAccessInfos.size").description("현재 페이지 크기"),
								fieldWithPath("logAccessInfos.number").description("현재 페이지 번호"),
								fieldWithPath("logAccessInfos.numberOfElements").description("현재 페이지의 데이터 수"),
								fieldWithPath("logAccessInfos.first").description("첫 번째 페이지 여부"),
								fieldWithPath("logAccessInfos.last").description("마지막 페이지 여부"),
								fieldWithPath("logAccessInfos.empty").description("페이지 비어 있는지 여부"),

								subsectionWithPath("logAccessInfos.pageable.sort").description("정렬 정보(empty/sorted/unsorted)"),
								subsectionWithPath("logAccessInfos.sort").description("현재 페이지 정렬 정보(empty/sorted/unsorted)")
						)
						.build()
				)
			));
	}

	@Test
	@DisplayName("로그 로그인 목록 조회")
	void getLogLoginList() throws Exception {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<LogInfo.LogLogin> logLoginPage = new PageImpl<>(
			List.of(
				LogInfo.LogLogin.builder()
						.id("1")
						.memberId("TestUser1")
						.accessIp("127.0.0.1")
						.success(true)
						.createdAt(ZonedDateTime.now())
						.build(),
				LogInfo.LogLogin.builder()
						.id("2")
						.memberId("TestUser2")
						.accessIp("127.0.0.1")
						.success(true)
						.createdAt(ZonedDateTime.now())
						.build()
			),
			pageable,
			2
		);

		LogResponse.LogLoginPage response =
				LogResponse.LogLoginPage.builder()
						.logLoginInfos(logLoginPage)
						.build();

		when(logFacade.getLogLogin(any(), any())).thenReturn(logLoginPage);
		when(logDtoMapper.mapToLogLoginList(logLoginPage)).thenReturn(response);

		mockMvc.perform(get("/api/logs/logins")
				.param("page", "0")
				.param("size", "10")
				.param("sort", "createdAt")
				.param("direction", "DESC")
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.logLoginInfos").exists())
		.andExpect(jsonPath("$.logLoginInfos.content").isArray())
		.andExpect(jsonPath("$.logLoginInfos.content.length()").value(2))
		.andExpect(jsonPath("$.logLoginInfos.content[0].memberId").value("TestUser1"))
		.andDo(document("log/get-log-logins",
				resource(ResourceSnippetParameters.builder()
						.tag("logLogin")
						.responseFields(
								fieldWithPath("logLoginInfos.content").description("로그인로그 목록"),
								fieldWithPath("logLoginInfos.content[].id").description("로그인로그 ID"),
								fieldWithPath("logLoginInfos.content[].memberId").description("회원 ID"),
								fieldWithPath("logLoginInfos.content[].accessIp").description("접속 IP"),
								fieldWithPath("logLoginInfos.content[].success").description("로그인 성공 여부"),
								fieldWithPath("logLoginInfos.content[].createdAt").description("접속 일자"),

								fieldWithPath("logLoginInfos.pageable").description("페이징 정보"),
								fieldWithPath("logLoginInfos.pageable.pageNumber").description("현재 페이지 번호"),
								fieldWithPath("logLoginInfos.pageable.pageSize").description("페이지 크기"),
								fieldWithPath("logLoginInfos.pageable.offset").description("오프셋"),
								fieldWithPath("logLoginInfos.pageable.paged").description("페이징 여부"),
								fieldWithPath("logLoginInfos.pageable.unpaged").description("페이징 미적용 여부"),

								fieldWithPath("logLoginInfos.totalPages").description("총 페이지 수"),
								fieldWithPath("logLoginInfos.totalElements").description("총 데이터 수"),
								fieldWithPath("logLoginInfos.size").description("현재 페이지 크기"),
								fieldWithPath("logLoginInfos.number").description("현재 페이지 번호"),
								fieldWithPath("logLoginInfos.numberOfElements").description("현재 페이지의 데이터 수"),
								fieldWithPath("logLoginInfos.first").description("첫 번째 페이지 여부"),
								fieldWithPath("logLoginInfos.last").description("마지막 페이지 여부"),
								fieldWithPath("logLoginInfos.empty").description("페이지 비어 있는지 여부"),

								subsectionWithPath("logLoginInfos.pageable.sort").description("정렬 정보(empty/sorted/unsorted)"),
								subsectionWithPath("logLoginInfos.sort").description("현재 페이지 정렬 정보(empty/sorted/unsorted)")
						)
						.build()
				)
			));
	}

}