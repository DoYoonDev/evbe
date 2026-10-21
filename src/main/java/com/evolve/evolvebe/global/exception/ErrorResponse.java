package com.evolve.evolvebe.global.exception;

public record ErrorResponse(int status, String error, String code, String message) {
	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(
			errorCode.getStatus().value(),
			errorCode.getStatus().getReasonPhrase(),
			errorCode.getCode(),
			errorCode.getMessage()
		);
	}

	public static ErrorResponse of(ErrorCode errorCode, String customMessage) {
		return new ErrorResponse(
			errorCode.getStatus().value(),
			errorCode.getStatus().getReasonPhrase(),
			errorCode.getCode(),
			customMessage
		);
	}
}
