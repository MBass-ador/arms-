package com.basssoft.arms.account.service;

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

    /**
     * Sets up the test environment before each test
     */
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
        AccountDTO result = service.createAccount(accountDTO);
        assertNotNull(result);
        assertNotNull(result.getAccountId());
    }


    /**
     * Test method for {@link AccountSvcImpl#getAccount(int)}.
     */
    @Test
    public void testGetAccountById() {
        AccountDTO account = service.getAccount(1);
        assertNotNull(account);
    }


    /**
     * Test method for {@link AccountSvcImpl#getAllAccounts()}.
     */
    @Test
    public void testGetAllAccounts() {
        List<AccountDTO> accounts = service.getAllAccounts();
        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }


    /**
     * Test method for {@link AccountSvcImpl#updateAccount(AccountDTO)}.
     */
    @Test
    public void testUpdateAccount() {
        AccountDTO accountDTO = new AccountDTO();
        AccountDTO result = service.updateAccount(accountDTO);
        assertEquals(result, accountDTO);
    }


    /**
     * Test method for {@link AccountSvcImpl#deleteAccount(int)}.
     */
    @Test
    public void testDeleteAccount() {
        int result = service.deleteAccount(1);
        assertEquals(1, result);
    }

}
