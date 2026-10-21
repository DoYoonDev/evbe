package com.evolve.evolvebe.auth;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.evolve.evolvebe.auth.application.AuthFacade;
import com.evolve.evolvebe.auth.domain.AuthCommand;
import com.evolve.evolvebe.auth.domain.Token;
import com.evolve.evolvebe.auth.interfaces.AuthController;
import com.evolve.evolvebe.auth.interfaces.AuthDtoMapper;
import com.evolve.evolvebe.auth.interfaces.AuthRequest;
import com.evolve.evolvebe.auth.interfaces.AuthResponse;
import com.evolve.evolvebe.global.config.SecurityConfig;
import com.evolve.evolvebe.global.exception.CustomException;
import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.global.security.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(
	controllers = AuthController.class,
	excludeAutoConfiguration = SecurityAutoConfiguration.class,
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = {SecurityConfig.class, JwtFilter.class})
	}
)
@AutoConfigureRestDocs
@ActiveProfiles("test")
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockitoBean
	private AuthFacade authFacade;

	@MockitoBean
	private AuthDtoMapper authDtoMapper;

	@Nested
	@DisplayName("로그인")
	class Login {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("login: 성공")
			void login() throws Exception {
				// given
				AuthRequest.Login request = new AuthRequest.Login();
				request.setUserId("userId");
				request.setPassword("password");
				AuthCommand.Login command = new AuthCommand.Login();
				command.setUserId("userId");
				command.setPassword("password");
				Token.MemberToken memberToken = Token.MemberToken.builder()
					.accessToken("access-token")
					.refreshToken("refresh-token")
					.build();
				AuthResponse.TokenResponse response = AuthResponse.TokenResponse.builder()
					.accessToken("access-token")
					.build();
				ResponseCookie cookie = ResponseCookie.from("refresh-token", memberToken.getRefreshToken())
					.maxAge(1800)
					.sameSite("Lax")
					.httpOnly(true)
					.path("/")
					.build();

				when(authDtoMapper.from(any(AuthRequest.Login.class))).thenReturn(command);
				when(authFacade.generateLoginToken(any(AuthCommand.Login.class))).thenReturn(memberToken);
				when(authFacade.generateCookie(memberToken.getRefreshToken())).thenReturn(cookie);
				when(authDtoMapper.from(memberToken)).thenReturn(response);

				// when & then
				mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.accessToken").value("access-token"))
					.andExpect(header().exists(HttpHeaders.SET_COOKIE))
					.andDo(document("auth/login",
						resource(
							ResourceSnippetParameters.builder()
								.tag("auth")
								.requestFields(
									fieldWithPath("userId").description("사용자 ID"),
									fieldWithPath("password").description("사용자 비밀번호")
								)
								.responseFields(
									fieldWithPath("accessToken").description("액세스 토큰")
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
			@DisplayName("login: 잘못된 자격 증명")
			void loginFail_invalidCredentials() throws Exception {
				// given
				AuthRequest.Login request = mock(AuthRequest.Login.class);
				AuthCommand.Login command = mock(AuthCommand.Login.class);

				when(authDtoMapper.from(any(AuthRequest.Login.class))).thenReturn(command);
				when(authFacade.generateLoginToken(any(AuthCommand.Login.class))).thenThrow(
					new CustomException(ErrorCode.USER_OR_PASSWORD_NOT_MATCH)
				);

				// when & then
				mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(request)))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.message").value(ErrorCode.USER_OR_PASSWORD_NOT_MATCH.getMessage()))
					.andDo(document("auth/login-fail",
						resource(
							ResourceSnippetParameters.builder()
								.tag("auth")
								.requestFields(
									fieldWithPath("userId").description("사용자 이름"),
									fieldWithPath("password").description("사용자 비밀번호")
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
}
