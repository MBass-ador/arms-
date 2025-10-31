package com.basssoft.arms.account.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Implementation
 * for Account CRUD operations

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Service
public class AccountSvcImpl {



    /**
     * Create new Account

     * @param account AccountDTO
     * @return boolean success (default true right now)
     */
    public boolean createAccount(AccountDTO account){

        return true;
    }


    /**
     * Get Account by ID

     * @param accountId int
     * @return AccountDTO (empty right now)
     */
    public AccountDTO getAccount(int accountId) {

        return new AccountDTO();
    }

    /**
     * Get all Accounts

     * @return List<Account> (empty right now)
     */
    public List<Account> getAllAccounts() {

        List<Account> accounts = new ArrayList<>();

        return accounts;
    }


    /**
     * Update existing Account

     * @param account AccountDTO
     * @return boolean success (default true right now)
     */
    public boolean updateAccount(AccountDTO account) {

        return true;
    }


    /**
     * Delete Account by ID

     * @param accountId int
     * @return boolean success (default true right now)
     */
    public boolean deleteAccount(int accountId) {

        return true;
    }

}
