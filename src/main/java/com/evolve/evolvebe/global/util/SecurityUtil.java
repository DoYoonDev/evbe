package com.evolve.evolvebe.global.util;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.evolve.evolvebe.global.exception.ErrorCode;
import com.evolve.evolvebe.global.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class SecurityUtil {

	private SecurityUtil() {
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	public static Optional<UserDetails> getCurrentUser() {
		Authentication authentication = getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			return Optional.of((UserDetails)authentication.getPrincipal());
		}
		return Optional.empty();
	}

    /*
	public static void setErrorResponse(HttpServletResponse response, ErrorCode errorCode, int status) {
		if (response.isCommitted()) {
			log.warn("Response already committed. Cannot write error.");
			return;
		}
		response.setStatus(status);
		response.setContentType("application/json;charset=UTF-8");
		ErrorResponse errorResponse = ErrorResponse.of(errorCode);
		try {
			response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
			response.getWriter().flush();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
	*/

    public static void setErrorResponse(HttpServletResponse response, ErrorCode errorCode, int status) {
        if (response.isCommitted()) {
            log.warn("Response already committed. Cannot write error.");
            return;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ErrorResponse errorResponse = ErrorResponse.of(errorCode);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            var os = response.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
