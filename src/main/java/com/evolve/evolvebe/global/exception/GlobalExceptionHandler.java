package com.evolve.evolvebe.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
		ErrorResponse response = new ErrorResponse(
			exception.getStatus().value(),
			exception.getStatus().getReasonPhrase(),
			exception.getCode(),
			exception.getMessage()
		);

		return ResponseEntity
			.status(exception.getStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(response);
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException exception) {
		ErrorResponse response = ErrorResponse.of(ErrorCode.FORBIDDEN);

		return ResponseEntity
			.status(ErrorCode.FORBIDDEN.getStatus())
			.contentType(MediaType.APPLICATION_JSON)
			.body(response);
	}

//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleException(Exception exception) {
//		ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
//
//		return ResponseEntity
//			.internalServerError()
//			.contentType(MediaType.APPLICATION_JSON)
//			.body(response);
//	}

	/*
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) {
		//ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);

		// 1. 이미 응답이 완료되었는지 체크
		if (response.isCommitted()) {
			// 로그만 남기고 아무것도 하지 않음
			log.warn("Response already committed. Cannot handle exception: {}", exception.getMessage());
			return null;
		}

		// 2. 로깅 (stacktrace 포함)
		log.error("Unhandled exception occurred at [{} {}]: {}", request.getMethod(), request.getRequestURI(), exception.getMessage(), exception);

		// 3. 응답
		ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);

		return ResponseEntity
				.internalServerError()
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorResponse);
	}
	 */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception exception) {
        log.error("Unhandled exception at [{} {}]", request.getMethod(), request.getRequestURI(), exception);
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(code));
    }
}
