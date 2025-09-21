package com.example.movieticket.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String auth = req.getHeader("Authorization");
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                var jws = jwtUtil.validateToken(token);
                Claims c = jws.getBody();
                String username = c.getSubject();
                String role = c.get("role", String.class);

                var authority = new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role);
                var authToken = new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception ex) {
                // invalid token -> leave SecurityContext empty (unauthenticated)
            }
        }
        chain.doFilter(req, res);
    }
}
