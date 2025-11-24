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

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * initializes test environment before each test
     */
    @BeforeEach
    public void setUp() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM invoice_bookings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM invoices").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM bookings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM accounts").executeUpdate();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        entityManager.flush();
    }


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
        AccountDTO dto = validDto();
        AccountDTO result = service.createAccount(dto);
        assertNotNull(result);
        assertNotNull(result.getAccountId());
        assertEquals(dto.getScreenName(), result.getScreenName());
        // verify persisted in repo
        Optional<Account> persisted = repo.findById(result.getAccountId());
        assertTrue(persisted.isPresent());
        assertEquals(result.getScreenName(), persisted.get().getScreenName());
    }


    /**
     * Test method for {@link AccountSvcImpl#getAccount(int)}.
     */
    @Test
    public void testGetAccountById() {
        AccountDTO created = service.createAccount(validDto());
        assertNotNull(created.getAccountId());
        AccountDTO fetched = service.getAccount(created.getAccountId());
        assertNotNull(fetched);
        assertEquals(created.getAccountId(), fetched.getAccountId());
        assertEquals(created.getEmail(), fetched.getEmail());
    }


    /**
     * Test method for {@link AccountSvcImpl#getAllAccounts()}.
     */
    @Test
    public void testGetAllAccounts() {
        // verify empty at start
        List<AccountDTO> empty = service.getAllAccounts();
        assertNotNull(empty);
        assertTrue(empty.isEmpty());
        // persist two accounts
        service.createAccount(validDto());
        AccountDTO dto2 = validDto();
        dto2.setScreenName("tester2");
        dto2.setEmail("tester2@example.com");
        service.createAccount(dto2);
        // verify get all returns two
        List<AccountDTO> all = service.getAllAccounts();
        assertEquals(2, all.size());
    }


    /**
     * Test method for {@link AccountSvcImpl#updateAccount(AccountDTO)}.
     */
    @Test
    public void testUpdateAccount() {
        // save account to db
        AccountDTO created = service.createAccount(validDto());
        assertNotNull(created.getAccountId());
        // update fields in retrieved dto
        created.setFirstName("Updated");
        created.setLastName("Name");
        // call update to persist changes
        AccountDTO updated = service.updateAccount(created);
        // verify returned dto
        assertNotNull(updated);
        assertEquals(created.getAccountId(), updated.getAccountId());
        assertEquals("Updated", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
        // verify persisted
        Optional<com.basssoft.arms.account.domain.Account> persisted = repo.findById(updated.getAccountId());
        assertTrue(persisted.isPresent());
        assertEquals("Updated", persisted.get().getFirstName());
    }


    /**
     * Test method for {@link AccountSvcImpl#deleteAccount(int)}.
     */
    @Test
    public void testDeleteAccount() {
        // save account to db
        AccountDTO created = service.createAccount(validDto());
        assertNotNull(created.getAccountId());
        // call delete
        int result = service.deleteAccount(created.getAccountId());
        // verify return value
        assertEquals(created.getAccountId(), result);
        // verify deleted from repo
        assertFalse(repo.findById(created.getAccountId()).isPresent());
    }

}
