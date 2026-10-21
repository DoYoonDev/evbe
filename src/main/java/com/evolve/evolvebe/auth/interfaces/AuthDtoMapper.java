package com.evolve.evolvebe.auth.interfaces;

import org.mapstruct.Mapper;

import com.evolve.evolvebe.auth.domain.AuthCommand;
import com.evolve.evolvebe.auth.domain.AuthInfo;
import com.evolve.evolvebe.auth.domain.Token;

@Mapper(
	componentModel = "spring",
	injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface AuthDtoMapper {

	AuthCommand.Login from(AuthRequest.Login request);

	AuthResponse.TokenResponse from(Token.MemberToken token);

	AuthResponse.InfoResponse from(AuthInfo.Main authInfo);

}
