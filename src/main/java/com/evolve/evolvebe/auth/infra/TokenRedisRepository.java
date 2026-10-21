package com.evolve.evolvebe.auth.infra;

import java.time.Duration;
import java.util.Optional;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class TokenRedisRepository {

	private final RedissonClient redissonClient;

	@Value("${security.jwt.refresh-expiration-time}")
	private Duration refreshTtl;

	public void saveRefreshToken(String userId, String refreshToken) {
		RBucket<String> bucket = redissonClient.getBucket(getKey(userId));
        bucket.set(refreshToken, refreshTtl);
    }

	public Optional<String> findRefreshToken(String userId) {
		RBucket<String> bucket = redissonClient.getBucket(getKey(userId));
        String val = bucket.get();
        if (val == null) {
            bucket.delete();
            return Optional.empty();
        }
        return Optional.of(val);
	}

    public void deleteRefreshToken(String userId) {
        if (isBlank(userId)) return;
        redissonClient.getBucket(getKey(userId)).delete();
    }

	private String getKey(String userId) {
		return "refresh-token:" + userId;
	}

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

}
