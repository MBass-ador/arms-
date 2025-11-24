package com.basssoft.arms.invoice.service;

import com.basssoft.arms.account.domain.Account;
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
    private InvoiceFactory invoiceFactory;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private IbookingService bookingService;

    private Account provider;
    private Account customer;
    private Account altProvider;
    private Account altCustomer;


    /**
     * Setup method initializes service before each test
     */
    @BeforeEach
    public void setUp() {
        // clean db state before each test
        bookingRepo.deleteAll();
        invoiceRepo.deleteAll();
        accountRepo.deleteAll();

        // create provider and customer accounts
        provider = new Account();
        provider.setScreenName("ProviderTest");
        provider.setFirstName("Provider");
        provider.setLastName("Test");
        provider.setEmail("providertest@example.com");
        provider.setPassword("password");
        provider.setPhoneNumber("000-000-0001");
        provider.setStreet("100 Provider Ln");
        provider.setCity("ProviderCity");
        provider.setState("PC");
        provider.setZipCode("11111");
        provider.setProvider(true);
        provider = accountRepo.save(provider);

        customer = new Account();
        customer.setScreenName("CustomerTest");
        customer.setFirstName("Customer");
        customer.setLastName("Test");
        customer.setEmail("customertest@example.com");
        customer.setPassword("password");
        customer.setPhoneNumber("000-000-0002");
        customer.setStreet("200 Customer Ave");
        customer.setCity("CustomerCity");
        customer.setState("CC");
        customer.setZipCode("22222");
        customer.setProvider(false);
        customer = accountRepo.save(customer);

        altProvider = new Account();
        altProvider.setScreenName("AltProvider");
        altProvider.setFirstName("Alt");
        altProvider.setLastName("Provider");
        altProvider.setEmail("altprovider@example.com");
        altProvider.setPassword("password");
        altProvider.setPhoneNumber("000-000-0003");
        altProvider.setStreet("300 Alt Ln");
        altProvider.setCity("AltCity");
        altProvider.setState("AP");
        altProvider.setZipCode("33333");
        altProvider.setProvider(true);
        altProvider = accountRepo.save(altProvider);

        altCustomer = new Account();
        altCustomer.setScreenName("AltCustomer");
        altCustomer.setFirstName("Alt");
        altCustomer.setLastName("Customer");
        altCustomer.setEmail("altcustomer@example.com");
        altCustomer.setPassword("password");
        altCustomer.setPhoneNumber("000-000-0004");
        altCustomer.setStreet("400 Alt Ave");
        altCustomer.setCity("AltCustomerCity");
        altCustomer.setState("AC");
        altCustomer.setZipCode("44444");
        altCustomer.setProvider(false);
        altCustomer = accountRepo.save(altCustomer);

        // common start time for bookings (one day ago)
        LocalDateTime baseStart = LocalDateTime.now().minusDays(1).withNano(0);

        // create bookings between definded providers and customers
        BookingDTO booking1 = new BookingDTO();
        booking1.setProvider(provider.getAccountId());
        booking1.setCustomer(customer.getAccountId());
        booking1.setHourlyRate(new BigDecimal("75.00"));
        booking1.setStartTime(baseStart);
        booking1.setEndTime(baseStart.plusHours(2)); // 2 hours
        booking1.setCompleted(true);
        booking1.setOverHours(new BigDecimal("0.00"));
        booking1.setPaid(false);
        booking1.setLocStreet("100 Provider Ln");
        booking1.setLocCity("ProviderCity");
        booking1.setLocState("PC");
        booking1.setLocZipCode("11111");
        booking1 = bookingService.createBooking(booking1);

        BookingDTO booking2 = new BookingDTO();
        booking2.setProvider(provider.getAccountId());
        booking2.setCustomer(customer.getAccountId());
        booking2.setHourlyRate(new BigDecimal("60.00"));
        booking2.setStartTime(baseStart.plusHours(3));
        booking2.setEndTime(baseStart.plusHours(6)); // 3 hours
        booking2.setCompleted(true);
        booking2.setOverHours(new BigDecimal("0.50"));
        booking2.setPaid(false);
        booking2.setLocStreet("200 Customer Ave");
        booking2.setLocCity("CustomerCity");
        booking2.setLocState("CC");
        booking2.setLocZipCode("22222");
        booking2 = bookingService.createBooking(booking2);

        BookingDTO booking3 = new BookingDTO();
        booking3.setProvider(altProvider.getAccountId());
        booking3.setCustomer(altCustomer.getAccountId());
        booking3.setHourlyRate(new BigDecimal("80.00"));
        booking3.setStartTime(baseStart.plusHours(7));
        booking3.setEndTime(baseStart.plusHours(11)); // 4 hours
        booking3.setCompleted(true);
        booking3.setOverHours(new BigDecimal("-0.25"));
        booking3.setPaid(false);
        booking3.setLocStreet("300 Alt Ln");
        booking3.setLocCity("AltCity");
        booking3.setLocState("AP");
        booking3.setLocZipCode("33333");
        booking3 = bookingService.createBooking(booking3);
    }


    /**
     * Test method for {@link InvoiceSvcImpl#createInvoice(InvoiceDTO)}.
     */
    @Test
    public void testCreateInvoice() {

        // use persisted booking/accounts created in setUp()
        Booking persisted = bookingRepo.findAll().stream()
                .filter(b -> "ProviderTest".equals(b.getProvider().getScreenName())
                        && "CustomerTest".equals(b.getCustomer().getScreenName())
                        && b.isCompleted()
                        && !b.isPaid())
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No completed & unpaid booking for " +
                                "ProviderTest/CustomerTest"));

        Account provider = persisted.getProvider();
        Account customer = persisted.getCustomer();

        // build an Invoice via factory, aggregating unpaid bookings
        Invoice built = invoiceFactory.createInvoice(provider, customer);

        // convert Invoice entity to DTO
        InvoiceDTO invoiceDTO = service._entityToDto(built);

        // persist via service and assert
        InvoiceDTO result = service.createInvoice(invoiceDTO);
        assertNotNull(result);
        assertNotNull(result.getInvoiceId());
        assertEquals(2, built.getBookings().size());
        assertTrue(built.getTotalAmountDue()
                .compareTo(result.getTotalAmountDue()) == 0);
        assertTrue(invoiceRepo.findById(result.getInvoiceId()).isPresent());
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#getInvoice(int)}.
     */
    @Test
    public void testGetInvoiceById() {

        Booking persisted = bookingRepo.findAll().stream()
                .filter(b -> "ProviderTest".equals(b.getProvider().getScreenName())
                        && "CustomerTest".equals(b.getCustomer().getScreenName())
                        && b.isCompleted()
                        && !b.isPaid())
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No completed & unpaid booking for ProviderTest/CustomerTest"));

        Account provider = persisted.getProvider();
        Account customer = persisted.getCustomer();

        // build Invoice via factory and persist via service to get an id
        Invoice built = invoiceFactory.createInvoice(provider, customer);

        InvoiceDTO createDto = service._entityToDto(built);

        InvoiceDTO created = service.createInvoice(createDto);
        assertNotNull(created);
        assertNotNull(created.getInvoiceId());

        // retrieve by the returned id and assert values
        InvoiceDTO fetched = service.getInvoice(created.getInvoiceId());
        assertNotNull(fetched);
        assertEquals(created.getInvoiceId(), fetched.getInvoiceId());
        assertTrue(built.getTotalAmountDue().compareTo(fetched.getTotalAmountDue()) == 0);
        assertTrue(invoiceRepo.findById(created.getInvoiceId()).isPresent());
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#getCustomerInvoices(int)}.
     */
    @Test
    public void testGetCustomerInvoices() {

        // Create and persist invoices for both customers
        InvoiceDTO invoice1 = service.createInvoice(service._entityToDto(invoiceFactory.createInvoice(provider, customer)));
        InvoiceDTO invoice2 = service.createInvoice(service._entityToDto(invoiceFactory.createInvoice(altProvider, altCustomer)));

        // Fetch invoices for 'customer'
        List<InvoiceDTO> customerInvoices = service.getCustomerInvoices(customer.getAccountId());
        assertNotNull(customerInvoices);
        assertTrue(customerInvoices.stream().allMatch(i -> i.getCustomerId().equals(customer.getAccountId())));
        assertTrue(customerInvoices.stream().anyMatch(i -> i.getInvoiceId().equals(invoice1.getInvoiceId())));
        assertTrue(customerInvoices.stream().noneMatch(i -> i.getInvoiceId().equals(invoice2.getInvoiceId())));
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#updateInvoice(InvoiceDTO)}.
     */
    @Test
    public void testUpdateInvoice() {

        // create and persist an invoice for provider/customer
        Invoice built = invoiceFactory.createInvoice(provider, customer);
        InvoiceDTO created = service.createInvoice(service._entityToDto(built));
        assertNotNull(created);
        assertNotNull(created.getInvoiceId());

        // update lastContacted and persist the update
        LocalDateTime now = LocalDateTime.now().withNano(0);
        created.setLastContacted(now);

        // call update and assert
        InvoiceDTO updated = service.updateInvoice(created);
        assertNotNull(updated);
        assertEquals(created.getInvoiceId(), updated.getInvoiceId());
        assertNotNull(updated.getLastContacted());
        assertEquals(0, now.compareTo(updated.getLastContacted()));
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#deleteInvoice(int)}.
     */
    @Test
    public void testDeleteInvoice() {

        // create and persist an invoice for provider/customer
        Invoice built = invoiceFactory.createInvoice(provider, customer);
        InvoiceDTO created = service.createInvoice(service._entityToDto(built));
        assertNotNull(created);
        assertNotNull(created.getInvoiceId());

        // delete the created invoice and assert result
        int result = service.deleteInvoice(created.getInvoiceId());
        assertEquals(created.getInvoiceId().intValue(), result);

        // verify the invoice no longer exists
        assertFalse(invoiceRepo.findById(created.getInvoiceId()).isPresent(),
                "Invoice should be removed from repository after delete");
        assertEquals(0, invoiceRepo.count(),
                "Expected repository to be empty after delete");

    }



}
