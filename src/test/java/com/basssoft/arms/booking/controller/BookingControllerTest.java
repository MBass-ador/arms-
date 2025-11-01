package com.basssoft.arms.booking.controller;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.service.IbookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for BookingController

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    static IbookingService bookingService = Mockito.mock(IbookingService.class);

    @TestConfiguration
    static class TestConfig {
        @Bean
        IbookingService bookingService() {
            return bookingService;
        }
    }

    // test data
    private Account provider;
    private Account customer;
    private BookingDTO bookingDto;
    private String bookingJson;

    /**
     * Set up test data before each test
     */
    @BeforeEach
    void setUp() {
        provider = new Account();
        provider.setAccountId(10);
        provider.setScreenName("providerUser");
        provider.setEmail("provider@email.com");

        customer = new Account();
        customer.setAccountId(20);
        customer.setScreenName("customerUser");
        customer.setEmail("customer@email.com");

        bookingDto = new BookingDTO();
        bookingDto.setBookingId(1);
        bookingDto.setProvider(provider);
        bookingDto.setCustomer(customer);

        bookingJson = """
            {
                "bookingId": 1,
                "provider": {
                    "accountId": 10,
                    "screenName": "providerUser",
                    "email": "provider@email.com"
                },
                "customer": {
                    "accountId": 20,
                    "screenName": "customerUser",
                    "email": "customer@email.com"
                }
            }
        """;
    }

    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#createBooking
     */
    @Test
    void testCreateBooking() throws Exception {

        // mock service method
        Mockito.when(bookingService.createBooking(Mockito.any(BookingDTO.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.provider.accountId").value(10))
                .andExpect(jsonPath("$.provider.screenName").value("providerUser"))
                .andExpect(jsonPath("$.provider.email").value("provider@email.com"))

                .andExpect(jsonPath("$.customer.accountId").value(20))
                .andExpect(jsonPath("$.customer.screenName").value("customerUser"))
                .andExpect(jsonPath("$.customer.email").value("customer@email.com"));
    }


    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#updateBooking:success
     */
    @Test
    void testUpdateBooking_Success() throws Exception {

        Mockito.when(bookingService.updateBooking(Mockito.any(BookingDTO.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(put("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider.accountId").value(10))
                .andExpect(jsonPath("$.customer.accountId").value(20));
    }


    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#updateBooking:bookingNotFound
     */
    @Test
    void testUpdateBooking_NotFound() throws Exception {

        Mockito.when(bookingService.updateBooking(Mockito.any(BookingDTO.class)))
                .thenReturn(null);

        mockMvc.perform(put("/bookings/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Booking not found."));
    }



}
