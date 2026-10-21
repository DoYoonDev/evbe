package com.evolve.evolvebe.global.exception;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@InjectMocks
	private GlobalExceptionHandler globalExceptionHandler;

	@Nested
	@DisplayName("CustomException 발생")
	class HandleCustomException {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("handleCustomException: 성공")
			void handleCustomException() {
				// given
				CustomException customException = new CustomException(ErrorCode.NOT_FOUND_MEMBER);

				// when
				ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomException(customException);

				// then
				assertEquals(ErrorCode.NOT_FOUND_MEMBER.getStatus().value(), response.getStatusCodeValue());
				assertEquals(ErrorCode.NOT_FOUND_MEMBER.getMessage(), response.getBody().message());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("handleCustomException: 실패")
			void handleCustomException() {
				// given
				CustomException customException = new CustomException(ErrorCode.NOT_FOUND_MEMBER);

				// when
				globalExceptionHandler.handleCustomException(customException);

				// then
				assertThat(ErrorCode.NOT_FOUND_MEMBER.getStatus().value()).isNotEqualTo(500);
			}
		}
	}

	@Nested
	@DisplayName("Exception 발생")
	class HandleException {

		@Nested
		@DisplayName("성공 케이스")
		class SuccessCase {

			@Test
			@DisplayName("handleException: 성공")
			void handleException() {
				// given
				MockHttpServletRequest request = new MockHttpServletRequest();
				MockHttpServletResponse response = new MockHttpServletResponse();
				Exception exception = new Exception();

				// when
				ResponseEntity<ErrorResponse> errResponse = globalExceptionHandler.handleException(request, exception);

				// then
				assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value(), errResponse.getStatusCodeValue());
				assertEquals(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), errResponse.getBody().message());
			}
		}

		@Nested
		@DisplayName("실패 케이스")
		class FailCase {

			@Test
			@DisplayName("handleException: 실패")
			void handleException() {
				// given
				MockHttpServletRequest request = new MockHttpServletRequest();
				MockHttpServletResponse response = new MockHttpServletResponse();
				Exception exception = new Exception();

				// when
				globalExceptionHandler.handleException(request, exception);

				// then
				assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value()).isNotEqualTo(400);
			}
		}
	}
}