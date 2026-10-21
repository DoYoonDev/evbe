package com.evolve.evolvebe.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final HttpStatus status;
	private final String code;
	private final String message;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}

	public CustomException(ErrorCode errorCode, String customMessage) {
		super(customMessage);
		this.status = errorCode.getStatus();
		this.code = errorCode.getCode();
		this.message = customMessage;
	}
}
