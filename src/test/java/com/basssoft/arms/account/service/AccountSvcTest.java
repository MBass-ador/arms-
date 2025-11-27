package com.basssoft.arms.account.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for {@link AccountSvcImpl}

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@SpringBootTest
@Transactional
public class AccountSvcTest {

    @Autowired
    private AccountSvcImpl service;

    @Autowired
    private AccountRepository repo;


    /**
     *  helper method
     *  creates valid AccountDTO
     * @return AccountDTO
     */
    private AccountDTO validDto() {
        AccountDTO dto = new AccountDTO();
        dto.setScreenName("testuser");
        dto.setPassword("password");
        dto.setProvider(false);
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("tuser@example.com");
        dto.setPhoneNumber("555-0100");
        dto.setStreet("1 Test Way");
        dto.setCity("Testville");
        dto.setState("TS");
        dto.setZipCode("12345");
        return dto;
    }

    /**
     * Test method for {@link AccountSvcImpl#createAccount(AccountDTO)}.
     */
    @Test
    public void testCreateAccount() {
        // create dto
        AccountDTO dto = new AccountDTO();
        dto.setScreenName("testuser");
        dto.setPassword("password");
        dto.setProvider(false);
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("tuser@example.com");
        dto.setPhoneNumber("555-0100");
        dto.setStreet("1 Test Way");
        dto.setCity("Testville");
        dto.setState("TS");
        dto.setZipCode("12345");
        // call create
        AccountDTO result = service.createAccount(dto);
        // verify returned dto
        assertNotNull(result);
        assertNotNull(result.getAccountId());
        assertEquals(dto.getScreenName(), result.getScreenName());
        // verify persisted correctly
        Optional<Account> persisted = repo.findById(result.getAccountId());
        assertTrue(persisted.isPresent());
        assertEquals(result.getScreenName(), persisted.get().getScreenName());
    }


    /**
     * Test method for {@link AccountSvcImpl#getAccountByScreenName(String)}.
     */
    @Test
    public void testGetAccountByScreenName() {

        // call method
        AccountDTO fetched = service.getAccountByScreenName("alice");
        // verify returned dto
        assertNotNull(fetched);
        assertEquals("alice", fetched.getScreenName());
    }



    /**
     * Test method for {@link AccountSvcImpl#getAccount(int)}.
     */
    @Test
    public void testGetAccountById() {
        AccountDTO alice = service.getAccountByScreenName("alice");
        assertNotNull(alice);
        int id = alice.getAccountId();

        AccountDTO fetched = service.getAccount(id);
        assertNotNull(fetched);
        assertEquals("alice", fetched.getScreenName());
    }


    /**
     * Test method for {@link AccountSvcImpl#getAllAccounts()}.
     */
    @Test
    public void testGetAllAccounts() {

        // call method
        List<AccountDTO> all = service.getAllAccounts();
        assertNotNull(all);

        // import.sql inserts 5 accounts: alice, bob, clara, derek, ernest
        assertTrue(all.size() >= 5);
        assertTrue(all.stream().anyMatch(a -> "alice".equals(a.getScreenName())));
        assertTrue(all.stream().anyMatch(a -> "bob".equals(a.getScreenName())));
        assertTrue(all.stream().anyMatch(a -> "clara".equals(a.getScreenName())));
        assertTrue(all.stream().anyMatch(a -> "derek".equals(a.getScreenName())));
        assertTrue(all.stream().anyMatch(a -> "ernest".equals(a.getScreenName())));
    }


    /**
     * Test method for {@link AccountSvcImpl#updateAccount(AccountDTO)}.
     */
    @Test
    public void testUpdateAccount() {
        // fetch existing account from import.sql
        AccountDTO alice = service.getAccountByScreenName("alice");
        assertNotNull(alice);
        // update fields
        alice.setFirstName("Updated");
        alice.setLastName("Name");
        // call update
        AccountDTO updated = service.updateAccount(alice);
        // verify returned dto
        assertNotNull(updated);
        assertEquals(alice.getAccountId(), updated.getAccountId());
        assertEquals("Updated", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        // verify persisted
        Optional<Account> persisted = repo.findById(updated.getAccountId());
        assertTrue(persisted.isPresent());
        assertEquals("Updated", persisted.get().getFirstName());
    }


    /**
     * Test method for {@link AccountSvcImpl#deleteAccount(int)}.
     */
    @Test
    public void testDeleteAccount() {
        // get existing account
        AccountDTO bob = service.getAccountByScreenName("bob");
        assertNotNull(bob);
        // call delete
        int result = service.deleteAccount(bob.getAccountId());
        // verify return value
        assertEquals(bob.getAccountId(), result);
        // verify deleted from repo
        assertFalse(repo.findById(bob.getAccountId()).isPresent());
    }

}
