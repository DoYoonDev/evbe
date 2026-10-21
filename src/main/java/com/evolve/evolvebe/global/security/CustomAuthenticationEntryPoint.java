package com.evolve.evolvebe.global.security;

import static com.evolve.evolvebe.global.util.SecurityUtil.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.evolve.evolvebe.global.exception.ErrorCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException
	) throws
		IOException,
		ServletException {
		setErrorResponse(response, ErrorCode.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
	}
}
