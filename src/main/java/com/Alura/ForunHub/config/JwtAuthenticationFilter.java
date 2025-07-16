package com.Alura.ForunHub.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // Ignorar endpoints de autenticação
        if (path.equals("/auth/login") || path.equals("/auth/register")) {
            filterChain.doFilter(request, response); // Passa direto para o próximo filtro
            return;
        }

        // Lógica de validação do JWT para outros endpoints
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring(7);
                UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
                if (authentication != null) {
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            Claims claims = Jwts
            .parser()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        String username = claims.getSubject();

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }
    }   catch (Exception e) {
        System.out.println("JWT Validation Error: " + e.getMessage());
    }

        return null;
    }
}