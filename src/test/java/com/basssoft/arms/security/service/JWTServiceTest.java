package com.basssoft.arms.security.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  tests for JWTService class
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class JWTServiceTest {

    // dependencies
    private JWTService jwtService;
    private UserDetails userDetails;

    /** setup before each test
     */
    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
        // Set test values for secret and expiration
        jwtService.setJwtSecret("testSecretKey12345678901234567890");
        jwtService.setJwtExpirationMs(3600000);
        userDetails = User.withUsername("testuser").password("password").authorities("ROLE_CUSTOMER").build();
    }



    /** tests generateToken() and isTokenValid() methods
     */
    @Test
    void testGenerateAndValidateToken() {

        // generate token
        String token = jwtService.generateToken(userDetails, Collections.emptyMap());
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);

        // validate token
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }



    /** tests isTokenExpired()
     */
    @Test
    void testExpiredToken() throws InterruptedException {

        // set short expiration for test (1ms)
        jwtService.setJwtExpirationMs(1);

        // get token
        String token = jwtService.generateToken(userDetails, Collections.emptyMap());

        // wait to ensure token expired
        Thread.sleep(5);

        // expired token should throw exception
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, userDetails);
        });
    }



    /** tests extractUsername() method
     */
    @Test
    void testExtractUsername() {

        // generate token
        String token = jwtService.generateToken(userDetails, Collections.emptyMap());

        // extract username
        String extractedUsername = jwtService.extractUsername(token);

        // verify
        assertEquals("testuser", extractedUsername);
    }




}
