package com.basssoft.arms.security.config;


import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *  used to filter and authenticate JWT tokens in incoming requests
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //dependencies
    private final JWTService jwtService;
    private final ArmsUserDetailsService userDetailsService;

    private static final Logger securityLogger = LoggerFactory.getLogger("com.basssoft.arms.security");

    /** constructor
     *  w dependency injection
     *
     * @param jwtService JWTService
     * @param userDetailsService ArmsUserDetailsService
     */
    public JwtAuthenticationFilter(JWTService jwtService, ArmsUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    /** filter authenticates JWT tokens
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException ServletException
     * @throws IOException IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // get auth header
        final String authHeader = request.getHeader("Authorization");

        // log request
        securityLogger.info("JwtAuthenticationFilter: Request URI={}", request.getRequestURI());

        // declare variables
        String jwt = null;
        String username = null;

        // extract JWT from cookies
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // if no JWT, continue filter chain
        if (jwt == null || jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // extract username from JWT
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            securityLogger.warn("Invalid JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        // validate and authorize
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // get current Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // get authorities
        if (authentication != null) {
            var authorities = authentication.getAuthorities();

            securityLogger.info("JwtAuthenticationFilter: Authenticated user '{}' " +
                            "with valid JWT, SecurityContext set, authorities={}",
                            username, authentication.getAuthorities());
        }

        filterChain.doFilter(request, response);
    }



    /** exclude /auth/* paths from filtering
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }


}
