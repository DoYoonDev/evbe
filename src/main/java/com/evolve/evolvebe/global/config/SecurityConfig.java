package com.evolve.evolvebe.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.evolve.evolvebe.global.security.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CorsConfigurationSource corsConfigurationSource;
	private final JwtFilter jwtFilter;
	private final AuthenticationEntryPoint authenticationEntryPoint;
	private final AccessDeniedHandler accessDeniedHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(
		HttpSecurity http,
		AuthenticationManager authenticationManager,
		AuthorizationManager<RequestAuthorizationContext> authorizationManager
	) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/members/create").permitAll()	// 임시추가
					.requestMatchers("/api/members/chkId/**").permitAll()	// 임시추가
					.requestMatchers("/api/terms/getTos").permitAll()
					.requestMatchers("/api/terms/getTop").permitAll()
					.requestMatchers("/api/auth/login").permitAll()
					.requestMatchers("/api/auth/refresh-token").permitAll()
					.requestMatchers("/docs/**").permitAll()
					.anyRequest().authenticated()
				// TODO : Menu 관리시 FE에서 필요한 URL과 API가 달라 별도의 API 권한 체크 필요
				// .anyRequest().access(authorizationManager)
			)
			.authenticationManager(authenticationManager)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			// 401, 403 custom handler
			.exceptionHandling(handler ->
				handler.authenticationEntryPoint(authenticationEntryPoint)
					.accessDeniedHandler(accessDeniedHandler)
			);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

}