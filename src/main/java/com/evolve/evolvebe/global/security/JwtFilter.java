package com.evolve.evolvebe.global.security;

import com.evolve.evolvebe.auth.infra.BearerAuthorizationExtractor;
import com.evolve.evolvebe.auth.infra.JwtProvider;
import com.evolve.evolvebe.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final CustomUserDetailService userDetailService;
	private final JwtProvider jwtProvider;
	private final BearerAuthorizationExtractor extractor;

	private static final String[] WHITELIST = {
			"/api/auth/login",
			"/api/auth/refresh-token"
	};

	@Override
	protected void doFilterInternal(final HttpServletRequest request,
		final HttpServletResponse response,
		final FilterChain filterChain)
		throws ServletException, IOException {

		final String requestUri = request.getRequestURI();
		if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isWhitelisted(requestUri)) {
			filterChain.doFilter(request, response);
			return;
		}

		final String authHeader = request.getHeader(AUTHORIZATION);
		if (authHeader == null || authHeader.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}

		/*
		final String accessToken = extractor.extractAccessToken(request.getHeader(AUTHORIZATION));
		if (!jwtProvider.validateToken(accessToken)) {
			SecurityContextHolder.clearContext();
			throw new BadCredentialsException("Invalid JWT token");
			//throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
		}
		 */
		final String accessToken = extractor.extractAccessToken(authHeader);
		if (accessToken == null || accessToken.isBlank()) {
			SecurityContextHolder.clearContext();
			filterChain.doFilter(request, response);
			return;
		}

		/*
		final String userId = jwtProvider.getSubject(accessToken);
		final Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
		if (userId != null && (currentAuth == null || currentAuth instanceof AnonymousAuthenticationToken)) {
			final UserDetails userDetails = userDetailService.loadUserByUsername(userId);
			final UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}
		filterChain.doFilter(request, response);
		 */

		try {
			jwtProvider.ensureValidAccessToken(accessToken);

			final String userId = jwtProvider.getSubject(accessToken);
			final Authentication current = SecurityContextHolder.getContext().getAuthentication();

			if (userId != null && (current == null || current instanceof AnonymousAuthenticationToken)) {
				final UserDetails userDetails = userDetailService.loadUserByUsername(userId);
				final UsernamePasswordAuthenticationToken authToken =
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}

			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException ex) {
			SecurityContextHolder.clearContext();
			writeUnauthorized(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
		} catch (JwtException | IllegalArgumentException ex) {
			SecurityContextHolder.clearContext();
			writeUnauthorized(response, ErrorCode.INVALID_ACCESS_TOKEN);
		} catch (Exception ex) {
			SecurityContextHolder.clearContext();
			writeUnauthorized(response, ErrorCode.UNAUTHORIZED);
		}
	}

	private boolean isWhitelisted(String uri) {
		if (uri == null) return false;
		for (String path : WHITELIST) {
			if (uri.equals(path)) {
				return true;
			}
		}
		return false;
	}

	private void writeUnauthorized(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		var body = Map.of(
				"code", errorCode.getCode(),
				"message", errorCode.getMessage()
		);

		new com.fasterxml.jackson.databind.ObjectMapper().writeValue(response.getWriter(), body);
	}

}
