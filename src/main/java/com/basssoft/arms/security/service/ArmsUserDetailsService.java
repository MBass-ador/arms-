package com.basssoft.arms.security.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.security.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Custom UserDetailsService implementation
 * for loading user data during authentication
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@Service
public class ArmsUserDetailsService implements UserDetailsService {

    // user data store
    private final AccountRepository accountRepository;


    /**
     * constructor for ArmsUserDetailsService
     * injects AccountRepository

     * @param accountRepository AccountRepository
     */
    @Autowired
    public ArmsUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     * load user by screenName(username)
     *
     * @param username String
     * @return UserDetails
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {

        // get user from repository
        Account account = getAccountByScreenName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // assign roles (customer only or provider/customer)
        var authorities = account.isProvider()
                ? List.of(
                new SimpleGrantedAuthority("ROLE_PROVIDER"),
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        )
                : List.of(
                new SimpleGrantedAuthority("ROLE_CUSTOMER")
        );

        // return UserDetails object
        return org.springframework.security.core.userdetails.User
                .withUsername(account.getScreenName())
                .password(account.getPassword())
                .authorities(authorities)
                .build();
    }


    /**
     * get Account by passed screen name
     *
     * @param screenName String
     * @return Optional<Account>
     */
    public Optional<Account> getAccountByScreenName(String screenName) {

        // call custom repository method
        return accountRepository.findByScreenName(screenName);
    }




}
