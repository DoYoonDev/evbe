package com.evolve.evolvebe.auth.domain;

public interface AuthenticationProvider {
	void authenticate(AuthCommand.Login command);

	String generateAccessToken(String userId);

	String generateRefreshToken(String userId);

	void saveRefreshToken(String userId, String refreshToken);

	boolean validateToken(String token);

	String extractToken(String authorizationHeader);

	String regenerateToken(String refreshToken);

	String extractUserId(String token);

    void revokeAllForUser(String userId);

    String extractUserIdAllowExpired(String token);

}
