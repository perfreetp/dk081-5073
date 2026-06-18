package com.safetycampus.config.security;

import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.context.UserContext;
import com.safetycampus.common.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(header);

        if (authHeader != null && authHeader.startsWith(prefix)) {
            String token = authHeader.substring(prefix.length());

            if (jwtUtil.validateToken(token)) {
                LoginUser loginUser = jwtUtil.parseToken(token);
                UserContext.setLoginUser(loginUser);

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + loginUser.getRoleType().name());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.remove();
        }
    }
}
