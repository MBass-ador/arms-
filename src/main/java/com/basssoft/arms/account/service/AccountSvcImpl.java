package com.basssoft.arms.account.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation
 * for Account CRUD operations

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Service
@Transactional
public class AccountSvcImpl implements IaccountService {

    @Autowired
    private AccountRepository repo;

    /** Create new Account

     * @param account AccountDTO
     * @return AccountDTO created account
     */
    public AccountDTO createAccount(AccountDTO account){

        // null check
        if (account == null) {
            return null;
        }
        // convert dto to account entity
        Account entity = dtoToEntity(account);

        // persist
        Account saved = repo.save(entity);

        // convert back to dto and return
        return entityToDto(saved);
    }


    /** Get Account by ID

     * @param accountId int
     * @return AccountDTO (just id right now)
     */
    public AccountDTO getAccount(int accountId) {

        // retrieve account entity
        Optional<Account> opt = repo.findById(accountId);

        // convert to dto and return (or null if not found)
        return opt.map(this::entityToDto).orElse(null);
    }


    /** Get all Accounts

     * @return List<AccountDTO> (empty list right now)
     */
    public List<AccountDTO> getAllAccounts() {

        // retrieve all accounts
        List<Account> entities = repo.findAll();

        // convert to list of dtos
        List<AccountDTO> accounts = new ArrayList<>(entities.size());
        for (Account a : entities) {
            AccountDTO dto = entityToDto(a);
            if (dto != null) {
                accounts.add(dto);
            }
        }
        return accounts;
    }


    /** Update existing Account

     * @param account AccountDTO
     * @return AccountDTO updated account
     */
    public AccountDTO updateAccount(AccountDTO account) {

        // null check
        if (account == null || account.getAccountId() == null) {
            return null;
        }
        // get existing account from repo (by id)
        Optional<Account> opt = repo.findById(account.getAccountId());

        // if not found, return null
        if (!opt.isPresent()) {
            return null;
        }
        // update fields from passed dto
        Account entity = opt.get();
        entity.setScreenName(account.getScreenName());
        entity.setPassword(account.getPassword());
        entity.setProvider(account.isProvider());
        entity.setFirstName(account.getFirstName());
        entity.setLastName(account.getLastName());
        entity.setEmail(account.getEmail());
        entity.setPhoneNumber(account.getPhoneNumber());
        entity.setStreet(account.getStreet());
        entity.setCity(account.getCity());
        entity.setState(account.getState());
        entity.setZipCode(account.getZipCode());

        // persist updated account entity
        Account saved = repo.save(entity);

        // convert back to dto and return
        return entityToDto(saved);
    }


    /** Delete Account by ID

     * @param accountId int
     * @return int deletedAccountId
     */
    public int deleteAccount(int accountId) {

        try {
            // delete by id
            repo.deleteById(accountId);
            // return deleted id
            return accountId;

        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            // no such entity
            return 0;
        }
    }





    /** helper
     *  converts DTO to Account entity
     *
     * @param dto AccountDTO
     * @return AccountDTO
     */
    private Account dtoToEntity(AccountDTO dto) {
        // null check
        if (dto == null) {
            return null;
        }
        // new account entity
        Account a = new Account();
        // only set id when present (create will let JPA generate)
        if (dto.getAccountId() != null) {
            a.setAccountId(dto.getAccountId());
        }
        // set fields from dto
        a.setScreenName(dto.getScreenName());
        a.setPassword(dto.getPassword());
        a.setProvider(dto.isProvider());
        a.setFirstName(dto.getFirstName());
        a.setLastName(dto.getLastName());
        a.setEmail(dto.getEmail());
        a.setPhoneNumber(dto.getPhoneNumber());
        a.setStreet(dto.getStreet());
        a.setCity(dto.getCity());
        a.setState(dto.getState());
        a.setZipCode(dto.getZipCode());
        return a;
    }

    /** helper
     *  converts Account entity to DTO
     *
     * @param a Account entity
     * @return AccountDTO
     */
    private AccountDTO entityToDto(Account a) {
        // null check
        if (a == null) {
            return null;
        }
        // new dto
        AccountDTO dto = new AccountDTO();
        // set fields from account entity
        dto.setAccountId(a.getAccountId());
        dto.setScreenName(a.getScreenName());
        dto.setPassword(a.getPassword());
        dto.setProvider(a.isProvider());
        dto.setFirstName(a.getFirstName());
        dto.setLastName(a.getLastName());
        dto.setEmail(a.getEmail());
        dto.setPhoneNumber(a.getPhoneNumber());
        dto.setStreet(a.getStreet());
        dto.setCity(a.getCity());
        dto.setState(a.getState());
        dto.setZipCode(a.getZipCode());
        return dto;
    }


}
