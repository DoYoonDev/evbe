package com.evolve.evolvebe.global.security;

import static com.evolve.evolvebe.global.util.SecurityUtil.*;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.evolve.evolvebe.global.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(
		HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws
		IOException,
		ServletException {
		setErrorResponse(response, ErrorCode.FORBIDDEN, HttpServletResponse.SC_FORBIDDEN);
	}
}

