package com.basssoft.arms.account.service;

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
     * @return AccountDTO created account
     */
    public AccountDTO createAccount(AccountDTO account){

        return account;
    }


    /**
     * Get Account by ID

     * @param accountId int
     * @return AccountDTO (just id right now)
     */
    public AccountDTO getAccount(int accountId) {

        AccountDTO created = new AccountDTO();
        created.setAccountId(accountId);
        return created;
    }

    /**
     * Get all Accounts

     * @return List<AccountDTO> (empty list right now)
     */
    public List<AccountDTO> getAllAccounts() {

        List<AccountDTO> accounts = new ArrayList<>();

        return accounts;
    }


    /**
     * Update existing Account

     * @param account AccountDTO
     * @return AccountDTO updated account
     */
    public AccountDTO updateAccount(AccountDTO account) {

        return account;
    }


    /**
     * Delete Account by ID

     * @param accountId int
     * @return int deletedAccountId
     */
    public int deleteAccount(int accountId) {

        return accountId;
    }

}
