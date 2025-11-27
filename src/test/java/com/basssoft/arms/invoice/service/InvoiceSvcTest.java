package com.basssoft.arms.invoice.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.account.service.IaccountService;
import com.basssoft.arms.booking.domain.Booking;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.repository.BookingRepository;
import com.basssoft.arms.booking.service.IbookingService;
import com.basssoft.arms.invoice.domain.Invoice;
import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InvoiceSvcImpl}

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@SpringBootTest
@Transactional
public class InvoiceSvcTest {

    @Autowired
    private InvoiceSvcImpl service;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private IaccountService accountService;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private IbookingService bookingService;

    private AccountDTO providerClara;
    private AccountDTO customerAlice;
    private AccountDTO providerDerek;
    private AccountDTO customerBob;


    /**
     * Setup method initializes service before each test
     */
    @BeforeEach
    public void setUp() {

        // test accounts from import.sql
        List<AccountDTO> accounts = accountService.getAllAccounts();


        providerClara = accounts.stream()
                .filter(a -> "clara".equals(a.getScreenName()))
                .findFirst()
                .orElseThrow();

        customerAlice = accounts.stream()
                .filter(a -> "alice".equals(a.getScreenName()))
                .findFirst()
                .orElseThrow();

        providerDerek = accounts.stream()
                .filter(a -> "derek".equals(a.getScreenName()))
                .findFirst()
                .orElseThrow();

        customerBob = accounts.stream()
                .filter(a -> "bob".equals(a.getScreenName()))
                .findFirst()
                .orElseThrow();
    }



    
    /**
     * Test method for {@link InvoiceSvcImpl#getInvoice(int)}.
     */
    @Test
    public void testGetInvoiceById() {

        // get existing invoice from the DB where (provider_id=4, customer_id=1, total_amount_due=260.00)
        Invoice invoice = invoiceRepo.findAll().stream()
                .filter(i -> i.getProvider().getAccountId() == 4
                        && i.getCustomer().getAccountId() == 1
                        && i.getTotalAmountDue().compareTo(new BigDecimal("260.00")) == 0)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected invoice not found in test data"));

        // call service method
        InvoiceDTO fetched = service.getInvoice(invoice.getInvoiceId());

        // assert matching data
        assertNotNull(fetched);
        assertEquals(invoice.getInvoiceId(), fetched.getInvoiceId());
        assertEquals(invoice.getProvider().getAccountId(), fetched.getProviderId());
        assertEquals(invoice.getCustomer().getAccountId(), fetched.getCustomerId());
        assertEquals(0, invoice.getTotalAmountDue().compareTo(fetched.getTotalAmountDue()));
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#getCustomerInvoices(int)}.
     */
    @Test
    public void testGetCustomerInvoices() {

        // customerAlice (accountId=1) from import.sql
        int customerId = customerAlice.getAccountId();

        // fetch customer invoices
        List<InvoiceDTO> customerInvoices = service.getCustomerInvoices(customerId);

        assertNotNull(customerInvoices);
        assertFalse(customerInvoices.isEmpty());

        // all invoices should belong to customerAlice
        assertTrue(customerInvoices.stream()
                .allMatch(i -> i.getCustomerId().equals(customerId)));
    }



}

