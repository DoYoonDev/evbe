package com.evolve.evolvebe.global.security;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import com.evolve.evolvebe.role.application.RoleFacade;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationManger implements AuthorizationManager<RequestAuthorizationContext> {

	private final RoleFacade roleFacade;

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
		RequestAuthorizationContext context) {
		Authentication authentication = authenticationSupplier.get();

		String requestUri = context.getRequest().getRequestURI();

		List<String> userRoles = authentication.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.toList();

		List<String> allowedMenus = roleFacade.getMenusUrlByRoles(userRoles);
		return new AuthorizationDecision(allowedMenus.contains(requestUri));
	}
}
