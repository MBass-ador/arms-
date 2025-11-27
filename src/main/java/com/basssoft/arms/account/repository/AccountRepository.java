package com.basssoft.arms.account.repository;

import com.basssoft.arms.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Account entity

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // used by Account Service to find account by screen name
    Optional<Account> findByScreenName(String screenName);
}
