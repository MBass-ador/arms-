package com.basssoft.arms.account.service;

import com.basssoft.arms.account.domain.AccountDTO;

import java.util.List;

/**
 * Service Interface
 * for Account CRUD operations

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
public interface IaccountService {

    /**
     * Create new Account
     *
     * @param account AccountDTO
     * @return AccountDTO
     */
    public AccountDTO createAccount(AccountDTO account);

    /**
     * Get Account by ID
     *
     * @param accountId int
     * @return AccountDTO
     */
    public AccountDTO getAccount(int accountId);

    /**
     * Get all Accounts
     *
     * @return List<AccountDTO>
     */
    public List<AccountDTO> getAllAccounts();

    /**
     * Update existing Account
     *
     * @param account AccountDTO
     * @return AccountDTO updated
     */
    public AccountDTO updateAccount(AccountDTO account);

    /**
     * Delete Account by ID
     *
     * @param accountId int
     * @return int
     */
    public int deleteAccount(int accountId);
}
