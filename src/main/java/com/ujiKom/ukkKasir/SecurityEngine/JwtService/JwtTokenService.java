package com.ujiKom.ukkKasir.SecurityEngine.JwtService;

import com.ujiKom.ukkKasir.SecurityEngine.Constant.SecurityConstant;
import com.ujiKom.ukkKasir.SecurityEngine.Utility.JWTTokenProvider;
import com.ujiKom.ukkKasir.UserManagement.User.Domain.UserPrincipal;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
    private final JWTTokenProvider jwtTokenProvider;

    public JwtTokenService(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public JwtHeaderResponse getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        String jwtToken = jwtTokenProvider.generateJwtToken(userPrincipal);
        headers.add(SecurityConstant.JWT_TOKEN_HEADER, jwtToken);
        headers.add(SecurityConstant.JWT_REFRESH_TOKEN, jwtTokenProvider.generateRefreshToken(userPrincipal));
        return new JwtHeaderResponse(headers, jwtToken);
    }
}
