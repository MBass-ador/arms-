package com.basssoft.arms.booking.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.account.service.AccountSvcImpl;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.repository.BookingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BookingSvcImpl}

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@SpringBootTest
@Transactional
public class BookingSvcTest {

    @Autowired
    private BookingSvcImpl service;

    @Autowired
    private AccountSvcImpl accountService;

    @Autowired
    private BookingRepository repo;

    @Autowired
    AccountRepository accountRepo;


    /**
     *  helper method
     *  creates BookingDTO
     * @return BookingDTO
     */
    private BookingDTO validDto() {
        // provider: create if missing, otherwise reuse id
        AccountDTO createdProvider;
        // find existing provider
        var providerOpt = accountRepo.findAll()
                .stream()
                .filter(a -> "provider1".equals(a.getScreenName()))
                .findFirst();
        // reuse if found
        if (providerOpt.isPresent()) {
            Account a = providerOpt.get();
            createdProvider = new AccountDTO();
            createdProvider.setAccountId(a.getAccountId());
            createdProvider.setProvider(a.isProvider());
        } else {
            // create new provider
            AccountDTO providerDto = new AccountDTO();
            providerDto.setScreenName("provider1");
            providerDto.setEmail("provider1@example.com");
            providerDto.setFirstName("Provider");
            providerDto.setLastName("One");
            providerDto.setPassword("Password");
            providerDto.setPhoneNumber("555-000-0001");
            providerDto.setStreet("100 Service Lane");
            providerDto.setCity("Servetown");
            providerDto.setState("SV");
            providerDto.setZipCode("54321");
            providerDto.setProvider(true);
            createdProvider = accountService.createAccount(providerDto);
        }

        // customer: create if missing, otherwise reuse id
        AccountDTO createdCustomer;
        // find existing customer
        var customerOpt = accountRepo.findAll()
                .stream()
                .filter(a -> "customer1".equals(a.getScreenName()))
                .findFirst();
        // reuse if found
        if (customerOpt.isPresent()) {
            Account a = customerOpt.get();
            createdCustomer = new AccountDTO();
            createdCustomer.setAccountId(a.getAccountId());
            createdCustomer.setProvider(a.isProvider());
        } else {
            // create new customer
            AccountDTO customerDto = new AccountDTO();
            customerDto.setScreenName("customer1");
            customerDto.setEmail("customer1@example.com");
            customerDto.setFirstName("Customer");
            customerDto.setLastName("One");
            customerDto.setPassword("Password");
            customerDto.setPhoneNumber("555-000-0002");
            customerDto.setStreet("200 Client Ave");
            customerDto.setCity("Servetown");
            customerDto.setState("SV");
            customerDto.setZipCode("54321");
            customerDto.setProvider(false);
            createdCustomer = accountService.createAccount(customerDto);
        }
        // create booking dto
        BookingDTO dto = new BookingDTO();
        // set provider/customer references
        dto.setProvider(createdProvider.getAccountId());
        dto.setCustomer(createdCustomer.getAccountId());
        // set booking details
        dto.setHourlyRate(new BigDecimal("75.00"));
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0);
        LocalDateTime end = start.plusHours(2);
        dto.setStartTime(start);
        dto.setEndTime(end);
        // location fields (adjust names if different)
        dto.setLocStreet("100 Service Lane");
        dto.setLocCity("Servetown");
        dto.setLocState("SV");
        dto.setLocZipCode("54321");
        // status flags
        dto.setCompleted(false);
        dto.setOverHours(BigDecimal.ZERO);
        dto.setPaid(false);
        // return dto
        return dto;
    }

    /**
     * Test method for {@link BookingSvcImpl#createBooking(BookingDTO)}.
     */
    @Test
    public void testCreateBooking() {
        // Use provider "clara" (id=3) and customer "alice" (id=1) from import.sql
        Account provider = accountRepo.findAll().stream()
                .filter(a -> "clara".equals(a.getScreenName()))
                .findFirst().orElseThrow();
        Account customer = accountRepo.findAll().stream()
                .filter(a -> "alice".equals(a.getScreenName()))
                .findFirst().orElseThrow();
        // build booking dto
        BookingDTO dto = new BookingDTO();
        dto.setProvider(provider.getAccountId());
        dto.setCustomer(customer.getAccountId());
        dto.setHourlyRate(new BigDecimal("75.00"));
        dto.setStartTime(LocalDateTime.of(2025, 8, 1, 9, 0));
        dto.setEndTime(LocalDateTime.of(2025, 8, 1, 11, 0));
        dto.setLocStreet("123 Test St");
        dto.setLocCity("Springfield");
        dto.setLocState("IL");
        dto.setLocZipCode("62701");
        dto.setCompleted(false);
        dto.setOverHours(BigDecimal.ZERO);
        dto.setPaid(false);
        // call create
        BookingDTO result = service.createBooking(dto);
        // verify
        assertNotNull(result);
        assertNotNull(result.getBookingId());
        assertEquals(provider.getAccountId(), result.getProvider());
        assertEquals(customer.getAccountId(), result.getCustomer());
    }


    /**
     * Test method for {@link BookingSvcImpl#getBooking(int)}.
     */
    @Test
    public void testGetBookingById() {
        // find provider "clara", customer "alice"
        Account provider = accountRepo.findAll().stream()
                .filter(a -> "clara".equals(a.getScreenName()))
                .findFirst().orElseThrow();
        Account customer = accountRepo.findAll().stream()
                .filter(a -> "alice".equals(a.getScreenName()))
                .findFirst().orElseThrow();

        // find  booking between them
        var booking = repo.findAll().stream()
                .filter(b -> b.getProvider().getAccountId().equals(provider.getAccountId()) &&
                        b.getCustomer().getAccountId().equals(customer.getAccountId()))
                .findFirst().orElseThrow();

        // call method with that booking's id
        BookingDTO retrieved = service.getBooking(booking.getBookingId());

        // Verify
        assertNotNull(retrieved);
        assertEquals(booking.getBookingId(), retrieved.getBookingId());
        assertEquals(provider.getAccountId(), retrieved.getProvider());
        assertEquals(customer.getAccountId(), retrieved.getCustomer());
    }


    /**
     * Test method for {@link BookingSvcImpl#getCustomerBookings(int)}.
     */
    @Test
    public void testGetCustomerBookings() {
        // customer "alice" (id=1) from import.sql
        Account customer = accountRepo.findAll().stream()
                .filter(a -> "alice".equals(a.getScreenName()))
                .findFirst().orElseThrow();

        // call method
        List<BookingDTO> bookings = service.getCustomerBookings(customer.getAccountId());

        // assert 4 bookings for alice in import.sql
        assertNotNull(bookings);
        assertEquals(4, bookings.size());
        for (BookingDTO dto : bookings) {
            assertEquals(customer.getAccountId(), dto.getCustomer());
        }
    }


    /**
     * Test method for {@link BookingSvcImpl#updateBooking(BookingDTO)}.
     */
    @Test
    public void testUpdateBooking() {
        // get existing booking for provider "clara" and customer "alice"
        Account provider = accountRepo.findAll().stream()
                .filter(a -> "clara".equals(a.getScreenName()))
                .findFirst().orElseThrow();
        Account customer = accountRepo.findAll().stream()
                .filter(a -> "alice".equals(a.getScreenName()))
                .findFirst().orElseThrow();

        var booking = repo.findAll().stream()
                .filter(b -> b.getProvider().getAccountId().equals(provider.getAccountId()) &&
                        b.getCustomer().getAccountId().equals(customer.getAccountId()))
                .findFirst().orElseThrow();

        // convert to dto
        BookingDTO dto = service.getBooking(booking.getBookingId());

        // update
        dto.setStartTime(LocalDateTime.of(2025, 10, 1, 9, 0));
        dto.setProvider(4);

        // Call update
        BookingDTO updated = service.updateBooking(dto);

        // Verify
        assertNotNull(updated);
        assertEquals(dto.getBookingId(), updated.getBookingId());
        assertEquals(4, updated.getProvider());
        assertEquals(LocalDateTime.of(2025, 10, 1, 9, 0), updated.getStartTime());
    }


    /**
     * Test method for {@link BookingSvcImpl#deleteBooking(int)}.
     */
    @Test
    public void testDeleteBooking() {

        // 1st booking in import.sql not referenced by any invoice
        // they start with id=1001
        int bookingId = 1001;
        // call method
        int result = service.deleteBooking(bookingId);
        // assert deletion
        assertEquals(bookingId, result);
        assertNull(service.getBooking(bookingId));
    }

}
