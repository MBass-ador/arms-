package com.basssoft.arms.security.service;

import com.basssoft.arms.security.domain.RefreshToken;
import com.basssoft.arms.security.exception.InvalidRefreshTokenException;
import com.basssoft.arms.security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.Instant;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 *  unit tests for RefreshTokenService
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class RefreshTokenServiceTest {


    /**
     *  tests createRefreshToken()
     */
    @Test
    void testCreateRefreshToken() {
        RefreshTokenRepository repo = Mockito.mock(RefreshTokenRepository.class);
        RefreshTokenService service = new RefreshTokenService(repo);

        Mockito.when(repo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
        RefreshToken token = service.createRefreshToken(1);

        assertNotNull(token.getToken());
        assertEquals(1, token.getAccountId());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));
    }


    /**
     *  tests validateToken()
     */
    @Test
    void testValidateToken() {
        RefreshTokenRepository repo = Mockito.mock(RefreshTokenRepository.class);
        RefreshTokenService service = new RefreshTokenService(repo);

        // valid token: should not throw
        RefreshToken validToken = new RefreshToken(1L, "abc", Instant.now().plusSeconds(1000), 1);
        Mockito.when(repo.findByToken("abc")).thenReturn(Optional.of(validToken));
        assertDoesNotThrow(() -> service.validateToken("abc"));

        // expired token: should throw
        RefreshToken expiredToken = new RefreshToken(2L, "def", Instant.now().minusSeconds(1000), 1);
        Mockito.when(repo.findByToken("def")).thenReturn(Optional.of(expiredToken));
        assertThrows(com.basssoft.arms.security.exception.InvalidRefreshTokenException.class,
                () -> service.validateToken("def"));

        // missing token: should throw
        Mockito.when(repo.findByToken("missing")).thenReturn(Optional.empty());
        assertThrows(com.basssoft.arms.security.exception.InvalidRefreshTokenException.class,
                () -> service.validateToken("missing"));
    }


    /**
     *  tests getByToken() - refresh token exists
     */
    @Test
    void getByToken_Exists() {

        // arrange
        RefreshTokenRepository repo = mock(RefreshTokenRepository.class);
        RefreshTokenService service = new RefreshTokenService(repo);

        String tokenValue = "test-token";
        RefreshToken token = new RefreshToken(1L, tokenValue, Instant.now().plusSeconds(1000), 42);

        when(repo.findByToken(tokenValue)).thenReturn(Optional.of(token));

        // act
        RefreshToken result = service.getByToken(tokenValue);

        // assert
        assertNotNull(result);
        assertEquals(tokenValue, result.getToken());
        verify(repo).findByToken(tokenValue);
    }


    /**
     *  tests getByToken() - refresh token does not exist
     */
    @Test
    void getByToken_NotFound() {

        // arrange
        RefreshTokenRepository repo = mock(RefreshTokenRepository.class);
        RefreshTokenService service = new RefreshTokenService(repo);

        when(repo.findByToken("missing")).thenReturn(Optional.empty());

        assertThrows(InvalidRefreshTokenException.class, () -> service.getByToken("missing"));

        verify(repo).findByToken("missing");
    }



    /**
     *  tests deleteByAccountId()
     */
    @Test
    void testDeleteByAccountId() {
        RefreshTokenRepository repo = Mockito.mock(RefreshTokenRepository.class);
        RefreshTokenService service = new RefreshTokenService(repo);

        service.deleteByAccountId(42);

        Mockito.verify(repo, Mockito.times(1)).deleteByAccountId(42);
    }

}
