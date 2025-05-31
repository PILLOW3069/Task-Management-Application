package com.hr.TaskTracker.security;

import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomerUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   CustomerUserDetailsService userDetailsService,
                                   UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        Long userId = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            userId = jwtUtil.extractUserId(token);
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userOpt.get().getUsername());
                CustomerUserDetailsService.CustomUserDetails customDetails =
                        (CustomerUserDetailsService.CustomUserDetails) userDetails;

                if (jwtUtil.validateToken(token, customDetails.getUserId())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
