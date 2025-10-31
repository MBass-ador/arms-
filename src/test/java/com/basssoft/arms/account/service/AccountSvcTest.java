package com.basssoft.arms.account.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AccountSvcImpl}
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
public class AccountSvcTest {

    private AccountSvcImpl service;

    @BeforeEach
    public void setUp() {
        service = new AccountSvcImpl();
    }

    /**
     * Test method for {@link AccountSvcImpl#createAccount(AccountDTO)}.
     */
    @Test
    public void testCreateAccount() {
        AccountDTO accountDTO = new AccountDTO();
        boolean result = service.createAccount(accountDTO);
        assertTrue(result);
    }


    /**
     * Test method for {@link AccountSvcImpl#getAccount(int)}.
     */
    @Test
    public void testGetAccountById() {
        AccountDTO account = service.getAccount(1);
        assertNotNull(account);
    }


    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = service.getAllAccounts();
        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }


    /**
     * Test method for {@link AccountSvcImpl#updateAccount(AccountDTO)}.
     */
    @Test
    public void testUpdateAccount() {
        AccountDTO accountDTO = new AccountDTO();
        boolean result = service.updateAccount(accountDTO);
        assertTrue(result);
    }


    /**
     * Test method for {@link AccountSvcImpl#deleteAccount(int)}.
     */
    @Test
    public void testDeleteAccount() {
        boolean result = service.deleteAccount(1);
        assertTrue(result);
    }

}
