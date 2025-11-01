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
public class AccountSvcImpl implements IaccountService {



    /**
     * Create new Account

     * @param account AccountDTO
     * @return boolean success (default true right now)
     */
    public AccountDTO createAccount(AccountDTO account){

        AccountDTO created = new AccountDTO();

        created.setAccountId(1);

        return created;
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
     * @return AccountDTO updated account
     */
    public AccountDTO updateAccount(AccountDTO account) {

        AccountDTO updated = new AccountDTO();
        updated.setAccountId(account.getAccountId());

        return updated;
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
