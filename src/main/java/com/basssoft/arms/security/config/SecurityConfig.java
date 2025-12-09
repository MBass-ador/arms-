package com.basssoft.arms.security.config;

import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *  Security Configuration class
 *  defines beans needed for security implementation
 *  and configures SecurityFilterChain
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@Configuration
public class SecurityConfig {


    /**
     * Bean for PasswordEncoder using BCrypt

     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /** Bean for JwtAuthenticationFilter

     * @param jwtService JWTService
     * @param userDetailsService ArmsUserDetailsService
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JWTService jwtService, ArmsUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }



    /** Bean for AuthenticationEntryPoint handler

     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        };
    }


    /** Bean for AccessDeniedHandler

     * @return AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
        };
    }



    /** Bean for SecurityFilterChain

     *  once a detached front-end is introduced in lieu of server-side rendered views,
     *  CORS should be added to filter chain

     *  CSRF not needed for stateless/JWT

     * @param http HttpSecurity
     * @param jwtService JWTService
     * @param userDetailsService ArmsUserDetailsService
     * @return SecurityFilterChain
     * @throws Exception Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
                                            HttpSecurity http,
                                            JWTService jwtService,
                                            ArmsUserDetailsService userDetailsService
    ) throws Exception {
        http
            // disable CSRF for stateless APIs
            .csrf(csrf -> csrf.disable())

            // disable form login allow custom controller to handle /auth/login
            .formLogin(form -> form.disable())

            // stateless  session management
            .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // define access rules
            .authorizeHttpRequests(auth -> auth
                // unauthenticated access
                .requestMatchers("/auth/**", "/Arms.css").permitAll()
                // customer endpoints require CUSTOMER role
                .requestMatchers("/ArmsSPA/**").hasRole("CUSTOMER")
                 // provider endpoints require PROVIDER role
                .requestMatchers("/api/invoice-factory/**").hasRole("PROVIDER")
                // other endpoints require authentication
                .requestMatchers("/accounts/**", "/bookings/**", "/invoices/**").authenticated()
            )
            // filter in correct order
            .addFilterBefore(jwtAuthenticationFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class)

            // handle exceptions with beans defined above
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
        );

        return http.build();
    }



}
