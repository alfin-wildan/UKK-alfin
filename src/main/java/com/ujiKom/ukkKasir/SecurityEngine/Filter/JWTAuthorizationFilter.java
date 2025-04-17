package com.ujiKom.ukkKasir.SecurityEngine.Filter;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ujiKom.ukkKasir.GeneralComponent.Entity.HttpResponse;
import com.ujiKom.ukkKasir.SecurityEngine.TokenStoreService;
import com.ujiKom.ukkKasir.SecurityEngine.Utility.JWTTokenProvider;
import com.ujiKom.ukkKasir.UserManagement.User.UserEntity;
import com.ujiKom.ukkKasir.UserManagement.User.UserService;
import com.ujiKom.ukkKasir.UserManagement.UserAudit.UserAuditEntity;
import com.ujiKom.ukkKasir.UserManagement.UserAudit.UserAuditService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.List;

import static com.ujiKom.ukkKasir.SecurityEngine.Constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.ujiKom.ukkKasir.SecurityEngine.Constant.SecurityConstant.TOKEN_HEADER;
import static com.ujiKom.ukkKasir.GeneralComponent.Utility.ServiceHelper.dateFormatter;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private UserAuditEntity userAuditEntity;
    protected final JWTTokenProvider jwtTokenProvider;
    protected final UserService userService;
    protected final UserAuditService userAuditService;
    private final TokenStoreService tokenStoreService;

    public JWTAuthorizationFilter(JWTTokenProvider jwtTokenProvider, UserService userService, UserAuditService userAuditService, TokenStoreService tokenStoreService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userAuditService = userAuditService;
        this.tokenStoreService = tokenStoreService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        userAuditEntity = new UserAuditEntity(dateFormatter(), System.currentTimeMillis());

        try {
            if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
                response.setStatus(HttpStatus.OK.value());
            } else {
                String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                try {
                    String token = authorizationHeader.substring(TOKEN_HEADER.length());
                    String email = jwtTokenProvider.getSubject(token);
                    UserEntity user = userService.findByEmail(email);
                    if (user != null) {
                        HttpSession httpSession = request.getSession();
                        httpSession.setAttribute("email", email);

                        userAuditEntity.setUsername(user.getName());
                        if (jwtTokenProvider.isTokenValid(email, token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
                            Authentication authentication = jwtTokenProvider.getAuthentication(email, authorities, request);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        } else {
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized: Invalid session");
                            return;
                        }
                    } else {
                        SecurityContextHolder.clearContext();
                    }
                    filterChain.doFilter(request, response);
                } catch (TokenExpiredException e) {
                    HttpStatus status = HttpStatus.UNAUTHORIZED;
                    HttpResponse<Object> httpResponse = new HttpResponse<>(e.getMessage(), status.value(), status.getReasonPhrase().toUpperCase());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), httpResponse);
                }
            }
        } finally {
            afterRequest(requestWrapper, responseWrapper);
        }
    }


    private void afterRequest(ContentCachingRequestWrapper requestWrapper, ContentCachingResponseWrapper responseWrapper) {
         /*
            Uncomment this for get JWT from the request
            requestWrapper.getHeader("authorization");
         */

        String method = requestWrapper.getMethod();
        if (method.equals("GET")) {
            return;
        }

        Long timeTaken = System.currentTimeMillis() - userAuditEntity.getTimestamp();
        userAuditEntity.setTimeTaken(
                timeTaken +
                        "ms"
        );
        userAuditEntity.setMethod(method);
        userAuditEntity.setRemoteAddress(requestWrapper.getRemoteAddr());
        userAuditEntity.setUri(requestWrapper.getRequestURI());
        userAuditEntity.setHost(requestWrapper.getHeader("host"));
        userAuditEntity.setUserAgent(requestWrapper.getHeader("user-agent"));
        if (requestWrapper.getContentType() != null) {
            userAuditEntity.setReqContentType(requestWrapper.getContentType().split("/")[1].toUpperCase());
        }
        if (responseWrapper.getContentType() != null) {
            userAuditEntity.setRespContentType(responseWrapper.getContentType().split("/")[1].toUpperCase());
        }
        userAuditEntity.setStatus(responseWrapper.getStatus());
        userAuditService.saveAuditLog(userAuditEntity);
    }
}
