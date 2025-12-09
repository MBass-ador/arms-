package com.basssoft.arms.security.repository;

import com.basssoft.arms.security.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * RefreshTokenRepository
 * repository interface for RefreshToken entity
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {


    /** find RefreshToken by token string

     * @param token String
     * @return Optional<RefreshToken>
     */
    Optional<RefreshToken> findByToken(String token);


    /** delete RefreshToken by accountId

     * @param accountId Integer
     */
    void deleteByAccountId(Integer accountId);



}
