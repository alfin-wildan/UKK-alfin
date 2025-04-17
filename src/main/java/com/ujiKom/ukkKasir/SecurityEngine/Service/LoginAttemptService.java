package com.ujiKom.ukkKasir.SecurityEngine.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class LoginAttemptService {
    private final LoadingCache<String, Integer> loginAttemptCache;
    public static final int MAXIMUM_NUMBER_OF_ATTEMPTS = 3;
    public static final int ATTEMPTS_INCREMENT = 1;

    public LoginAttemptService() {
        super();
        loginAttemptCache  = CacheBuilder.newBuilder()
                .maximumSize(100).build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;
        try {
            attempts = ATTEMPTS_INCREMENT + loginAttemptCache.get(username);
//            System.out.println("attempts: " + attempts);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);

    }

    public boolean hasExceedMaxAttempts(String username) {
        try {
            this.addUserToLoginAttemptCache(username);
//            System.out.println("CHECKING ATTEMPTS");
            return loginAttemptCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
