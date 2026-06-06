package com.codequest.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseTokenFilter.class);
    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenFilter(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Это лог появится ПЕРВЫМ в консоли бэкенда
        logger.info(">>> [API REQUEST] {} {}", method, path);

        try {
            String token = extractToken(request);

            if (token != null) {
                try {
                    var decodedToken = firebaseAuth.verifyIdToken(token);
                    String uid = decodedToken.getUid();
                    
                    // Устанавливаем в ваш кастомный контекст
                    SecurityContext.setUserId(uid);
                    
                    // Устанавливаем в стандартный Spring Security контекст
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            uid, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.info(">>> User {} authenticated for path {}", uid, path);
                } catch (FirebaseAuthException e) {
                    logger.error(">>> Firebase Auth Error for {}: {}", path, e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid Firebase token");
                    return;
                }
            } else {
                logger.warn(">>> No Bearer token found for path: {}", path);
            }

            filterChain.doFilter(request, response);
        } finally {
            // Очищаем оба контекста после завершения запроса
            SecurityContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Разрешаем проходить без проверки токена для этих путей
        return path.contains("/health") || 
               path.contains("/swagger") || 
               path.contains("/v3/api-docs") || 
               path.contains("/docs");
    }
}
