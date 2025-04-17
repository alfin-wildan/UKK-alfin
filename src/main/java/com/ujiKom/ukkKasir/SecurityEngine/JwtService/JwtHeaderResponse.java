package com.ujiKom.ukkKasir.SecurityEngine.JwtService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtHeaderResponse {
    private HttpHeaders headers;
    private String jwtToken;
}
