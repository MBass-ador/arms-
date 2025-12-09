package com.basssoft.arms.security.service;


import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.security.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  unit tests for ArmsUserDetailsService
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class ArmsUserDetailsServiceTest {


    /**
     *  tests loadUserByUsername() when user is found
     */
    @Test
    void testLoadUserByUsername_found() {
        AccountRepository repo = Mockito.mock(AccountRepository.class);
        Account account = new Account();
        account.setScreenName("user");
        account.setPassword("pass");
        account.setProvider(true);
        Mockito.when(repo.findByScreenName("user")).thenReturn(Optional.of(account));

        ArmsUserDetailsService service = new ArmsUserDetailsService(repo);
        UserDetails details = service.loadUserByUsername("user");

        assertEquals("user", details.getUsername());
        assertEquals("pass", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROVIDER")));
    }


    /**
     *  tests loadUserByUsername() when user is not found
     */
    @Test
    void testLoadUserByUsername_notFound() {
        AccountRepository repo = Mockito.mock(AccountRepository.class);
        Mockito.when(repo.findByScreenName("missing")).thenReturn(Optional.empty());

        ArmsUserDetailsService service = new ArmsUserDetailsService(repo);
        assertThrows(UserNotFoundException.class, () -> service.loadUserByUsername("missing"));
    }


    /**
     *  tests getAccountByScreenName() when account is found
     */
    @Test
    void testGetAccountByScreenName_found() {
        // arrange
        AccountRepository repo = Mockito.mock(AccountRepository.class);
        Account account = new Account();
        account.setScreenName("user");

        Mockito.when(repo.findByScreenName("user")).thenReturn(Optional.of(account));

        ArmsUserDetailsService service = new ArmsUserDetailsService(repo);

        // act
        Optional<Account> result = service.getAccountByScreenName("user");

        // assert
        assertTrue(result.isPresent());
        assertEquals("user", result.get().getScreenName());
    }


    /**
     *  tests getAccountByScreenName() when account is not found
     */
    @Test
    void testGetAccountByScreenName_notFound() {
        // arrange
        AccountRepository repo = Mockito.mock(AccountRepository.class);
        Mockito.when(repo.findByScreenName("missing")).thenReturn(Optional.empty());

        ArmsUserDetailsService service = new ArmsUserDetailsService(repo);

        // act
        Optional<Account> result = service.getAccountByScreenName("missing");

        // assert
        assertFalse(result.isPresent());
    }





}
