package com.ujiKom.ukkKasir.SecurityEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TokenStoreService {
    private final ConcurrentHashMap<String, String> activeAccessTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> activeRefreshTokens = new ConcurrentHashMap<>();

    public void addAccessToken(String username, String token) {
//        log.info("Adding access token for user: {}", username);
        activeAccessTokens.put(username, token);
    }

    public void addRefreshToken(String username, String token) {
//        log.info("Adding refresh token for user: {}", username);
        activeRefreshTokens.put(username, token);
    }

    public boolean isAccessTokenValid(String username, String token) {
        boolean valid = token.equals(activeAccessTokens.get(username));
//        log.info("Checking access token validity for user: {} - {}", username, valid);
        return valid;
    }

    public boolean isRefreshTokenValid(String username, String token) {
        boolean valid = token.equals(activeRefreshTokens.get(username));
//        log.info("Checking refresh token validity for user: {} - {}", username, valid);
        return valid;
    }

    public void invalidateAccessToken(String username) {
//        log.info("Invalidating access token for user: {}", username);
        activeAccessTokens.remove(username);
    }

    public void invalidateRefreshToken(String username) {
//        log.info("Invalidating refresh token for user: {}", username);
        activeRefreshTokens.remove(username);
    }
    public boolean hasActiveToken(String username) {
        return activeAccessTokens.containsKey(username);
    }
}
