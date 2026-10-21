package com.evolve.evolvebe.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 400 (BAD_REQUEST)
	// Member
	NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "NOT_FOUND_MEMBER", "없는 계정입니다."),
	ALREADY_EXISTS_MEMBER_NAME(HttpStatus.BAD_REQUEST, "ALREADY_EXISTS_MEMBER_NAME", "이미 사용 중인 계정명입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", "기존 패스워드와 동일합니다."),
	PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_NOT_MATCH", "비밀번호가 일치하지 않습니다."),
    ALREADY_REGISTERED_MEMBER(HttpStatus.CONFLICT,  "ALREADY_REGISTERED_MEMBER", "이미 등록된 사용자입니다."),

	// Auth
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ACCESS_TOKEN", "유효하지 않은 액세스 토큰입니다."),
	EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_ACCESS_TOKEN", "만료된 액세스 토큰입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "EXPIRED_REFRESH_TOKEN", "만료된 리프레시 토큰입니다."),
	USER_OR_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER_OR_PASSWORD_NOT_MATCH", "아이디 또는 비밀번호가 일치하지 않습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "로그인 후 이용해주세요."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다."),
	NOT_FOUND_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "NOT_FOUND_AUTHENTICATION", "권한 정보를 확인할 수 없습니다."),

	// Message
	NOT_FOUND_MESSAGE(HttpStatus.NOT_FOUND, "NOT_FOUND_MESSAGE", "해당 메시지를 찾을 수 없습니다."),
	EXISTS_ALREADY_MESSAGE(HttpStatus.BAD_REQUEST, "EXISTS_ALREADY_MESSAGE", "이미 사용 중인 메시지를입니다."),
	ERROR_DEPLOY_MESSAGE(HttpStatus.BAD_REQUEST, "ERROR_DEPLOY_MESSAGE", "메시지 다국어 반영에 실패하였습니다."),

	// Code
	NOT_FOUND_CODE(HttpStatus.NOT_FOUND, "NOT_FOUND_CODE", "해당 코드를 찾을 수 없습니다."),
	NOT_FOUND_CODE_GROUP(HttpStatus.NOT_FOUND, "NOT_FOUND_CODE_GROUP", "해당 그룹 코드를 찾을 수 없습니다."),

	// Menu
	NOT_FOUND_MENU(HttpStatus.NOT_FOUND, "NOT_FOUND_MENU", "해당 메뉴를 찾을 수 없습니다."),
	NOT_FOUND_PARENT_MENU(HttpStatus.NOT_FOUND, "NOT_FOUND_PARENT_MENU", "상위 메뉴를 찾을 수 없습니다."),
	INVALID_MENU_HIERARCHY(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_MENU_HIERARCHY",
		"상위 메뉴가 없는 경우, 메뉴는 1레벨 메뉴로만 등록할 수 있습니다."),
	INVALID_PARENT_MENU_LEVEL(HttpStatus.BAD_REQUEST, "INVALID_PARENT_MENU_LEVEL", "상위 메뉴의 레벨은 등록하려는 메뉴보다 높아야 합니다."),

	// Role
	NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "NOT_FOUND_ROLE", "해당 역할을 찾을 수 없습니다."),
	NOT_FOUND_ROLE_MEMBER(HttpStatus.NOT_FOUND, "NOT_FOUND_ROLE_MEMBER", "해당 역할의 멤버를 찾을 수 없습니다."),

	// Board
	NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, "NOT_FOUND_BOARD", "해당 게시글을 찾을 수 없습니다."),
	NOT_FOUNT_ATTACHFILE(HttpStatus.NOT_FOUND, "NOT_FOUNT_ATTACHFILE", "첨부파일을 찾을 수 없습니다."),

	// 500
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "예상치 못한 내부 서버 오류입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
