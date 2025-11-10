package com.basssoft.arms.booking.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.account.service.AccountSvcImpl;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.repository.BookingRepository;
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
     * sets up the test environment before each test
     */
    @BeforeEach
    public void setUp()  {
        repo.deleteAll();
        accountRepo.deleteAll();
    }


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
        // create dto
        BookingDTO bookingDTO = validDto();
        // persist booking
        BookingDTO result = service.createBooking(bookingDTO);
        // verify
        assertNotNull(result);
        assertNotNull(result.getBookingId());
    }


    /**
     * Test method for {@link BookingSvcImpl#getBooking(int)}.
     */
    @Test
    public void testGetBookingById() {
        // persist booking
        BookingDTO created = service.createBooking(validDto());
        // verify created
        assertNotNull(created.getBookingId());
        // retrieve booking by id
        BookingDTO booking = service.getBooking(created.getBookingId());
        // verify
        assertNotNull(booking);
        assertEquals(created.getBookingId(), booking.getBookingId());
    }


    /**
     * Test method for {@link BookingSvcImpl#getAllBookings()}.
     */
    @Test
    public void testGetAllBookings() {
        // persist two bookings
        service.createBooking(validDto());
        BookingDTO secondDTO = validDto();
        service.createBooking(validDto());
        // retrieve all bookings
        List<BookingDTO> bookings = service.getAllBookings();
        // verify
        assertNotNull(bookings);
        assertEquals(2, bookings.size());
    }


    /**
     * Test method for {@link BookingSvcImpl#updateBooking(BookingDTO)}.
     */
    @Test
    public void testUpdateBooking() {
        // create and persist booking
        BookingDTO created = service.createBooking(validDto());
        assertNotNull(created.getBookingId());
        // update some fields
        created.setHourlyRate(new BigDecimal("80.00"));
        created.setCompleted(true);
        // call update
        BookingDTO result = service.updateBooking(created);
        // verify
        assertNotNull(result);
        assertEquals(created.getBookingId(), result.getBookingId());
        assertEquals(new BigDecimal("80.00"), result.getHourlyRate());
        assertTrue(result.isCompleted());
    }


    /**
     * Test method for {@link BookingSvcImpl#deleteBooking(int)}.
     */
    @Test
    public void testDeleteBooking() {
        // persist a booking
        BookingDTO created = service.createBooking(validDto());
        // verify created
        assertNotNull(created.getBookingId());
        // delete booking
        int result = service.deleteBooking(created.getBookingId());
        // verify response
        assertEquals(created.getBookingId(), result);
        // make sure it's gone
        BookingDTO deleted = service.getBooking(created.getBookingId());
        assertNull(deleted);
    }

}
