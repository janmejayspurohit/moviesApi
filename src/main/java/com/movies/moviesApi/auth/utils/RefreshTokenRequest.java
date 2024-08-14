package com.movies.moviesApi.auth.utils;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;
}
