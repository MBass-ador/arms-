package com.basssoft.arms.security.service;

import com.basssoft.arms.security.domain.RefreshToken;
import com.basssoft.arms.security.exception.InvalidRefreshTokenException;
import com.basssoft.arms.security.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * RefreshTokenService
 * service class for managing refresh tokens

 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@Service
public class RefreshTokenService {

    // refresh token repo
    private final RefreshTokenRepository refreshTokenRepository;

    /** constructor
     *
     * @param refreshTokenRepository RefreshTokenRepository
     */
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /** create new RefreshToken for passed accountId
     *
     * @param accountId Integer
     * @return RefreshToken
     */
    public RefreshToken createRefreshToken(Integer accountId) {

        // new token
        RefreshToken token = new RefreshToken();

        // set values
        token.setAccountId(accountId);
        token.setToken(UUID.randomUUID().toString());
        // expire in 7 days
        token.setExpiryDate(Instant.now().plusSeconds(604800));

        return refreshTokenRepository.save(token);
    }


    /** validate refresh token
     *
     * @param token String
     */
    public void validateToken(String token) {

        if (!refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .isPresent()) {
            throw new InvalidRefreshTokenException("Refresh token is invalid or expired");
        }
    }


    /** get RefreshToken by passed token value
     *
     * @param token String
     * @return RefreshToken
     */
    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));
    }


    /** delete refresh token by accountId
     *
     * @param accountId Integer
     */
    public void deleteByAccountId(Integer accountId) {

        // delete token from repo
        refreshTokenRepository.deleteByAccountId(accountId);
    }

}
