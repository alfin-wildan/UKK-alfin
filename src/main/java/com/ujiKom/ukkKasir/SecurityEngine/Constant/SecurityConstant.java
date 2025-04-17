package com.ujiKom.ukkKasir.SecurityEngine.Constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 600000;
    public static final long EXPIRATION_TIME_RT = 86400000;
    public static final String TOKEN_HEADER = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified!";
    public static final String ISSUER_COMPANY = "DesaCode";
    public static final String GET_ARRAYS_ADMINISTRATION = "Internal Monitoring System";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to log in to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String PUBLIC_URLS = "/**";
    public static final String LOGIN_URLS = "/user/login";
    public static final String LOGOUT_URLS = "/user/logout";
    public static final String JWT_REFRESH_TOKEN = "Refresh-Token";
    public static final String REFRESH_TOKEN_IS_MISSING = "Refresh Token is missing";
}
