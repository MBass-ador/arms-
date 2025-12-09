package com.basssoft.arms.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * JWTService
 * service class for managing JWT tokens
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@Setter
@Service
public class JWTService {

    // JWT secret key
    @Value("${jwt.secret:defaultSecretKey12345678901234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}") // 1 hour default
    private long jwtExpirationMs;


    /** get signing key for JWT

     * @return Key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    /** generate JWT token for user

     * @param userDetails UserDetails
     * @param extraClaims Map<String, Object>
     * @return String
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /** get screenname(username) from JWT token

     * @param token String
     * @return io.jsonwebtoken.Claims
     */
    public String extractUsername(String token) {

        return _parseClaims(token).getSubject();
    }



    /** check if token is valid for user

     * @param token String
     * @param userDetails UserDetails
     * @return io.jsonwebtoken.Claims
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    /** check if token is expired

     * @param token String
     * @return io.jsonwebtoken.Claims
     */
    private boolean isTokenExpired(String token) {
        return _parseClaims(token).getExpiration().before(new Date());
    }


    /** parse claims from JWT token

     * @param token String
     * @return io.jsonwebtoken.Claims
     */
    private Claims _parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }














}
