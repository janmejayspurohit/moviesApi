package com.movies.moviesApi.auth.services;

import com.movies.moviesApi.auth.entities.RefreshToken;
import com.movies.moviesApi.auth.entities.User;
import com.movies.moviesApi.auth.repositories.RefreshTokenRepository;
import com.movies.moviesApi.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        RefreshToken refreshToken = user.getRefreshToken();
        if (refreshToken == null) {
            long refreshTokenValidity = 5 * 60 * 60 * 10000;
            refreshToken = RefreshToken.builder()
                                       .refreshToken(UUID.randomUUID().toString())
                                       .expiry(Instant.now().plusMillis(refreshTokenValidity))
                                       .build();
        }

        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                                                      .orElseThrow(() -> new RuntimeException(
                                                              "Refresh Token not found"));
        if (refToken.getExpiry().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException(("Refresh Token Expired"));
        }

        return refToken;
    }
}
