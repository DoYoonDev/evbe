package com.evolve.evolvebe.global.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.evolve.evolvebe.member.application.MemberFacade;
import com.evolve.evolvebe.member.domain.MemberInfo;
import com.evolve.evolvebe.role.application.RoleFacade;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final MemberFacade memberFacade;
	private final RoleFacade roleFacade;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MemberInfo.Main member = memberFacade.getMember(username);

		List<GrantedAuthority> authorities = roleFacade.getMemberRoles(member.getId()).stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());

		if (authorities.isEmpty()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}

		return User.builder()
			.username(member.getId())
			.password(member.getPassword())
			.authorities(authorities)
			.build();
	}
}
