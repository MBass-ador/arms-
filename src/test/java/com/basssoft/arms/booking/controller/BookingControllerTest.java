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
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for BookingController

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@WebMvcTest(BookingController.class)
@Import(BookingControllerTest.TestConfig.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IbookingService bookingService;

    @Autowired
    private BookingModelAssembler bookingModelAssembler;

    @TestConfiguration
    static class TestConfig {
        @Bean
        IbookingService bookingService() {
            return Mockito.mock(IbookingService.class);
        }
        @Bean
        BookingModelAssembler bookingModelAssembler() {
            return Mockito.mock(BookingModelAssembler.class);
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

        int providerId = 10;
        int customerId = 20;

        bookingDto = new BookingDTO();
        bookingDto.setBookingId(1);
        bookingDto.setProvider(providerId);
        bookingDto.setCustomer(customerId);

        bookingJson = """
        {
            "bookingId": 1,
            "provider": 10,
            "customer": 20
        }
    """;

        Mockito.when(bookingModelAssembler.toModel(Mockito.any(BookingDTO.class)))
                .thenAnswer(invocation ->
                        EntityModel.of(
                                invocation.getArgument(0),
                                linkTo(methodOn(BookingController.class)
                                        .getBooking(1))
                                        .withSelfRel()
                        )
                );
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
                .andExpect(jsonPath("$.bookingId").exists())
                .andExpect(jsonPath("$.provider").value(10))
                .andExpect(jsonPath("$.customer").value(20))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#getBooking:success
     */
    @Test
    void testGetBooking_Success() throws Exception {

        Mockito.when(bookingService.getBooking(1)).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.provider").value(10))
                .andExpect(jsonPath("$.customer").value(20))
                .andExpect(jsonPath("$._links.self.href").exists());
    }


    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#getBooking:bookingNotFound
     */
    @Test
    void testGetBooking_NotFound() throws Exception {

        Mockito.when(bookingService.getBooking(99)).thenReturn(null);

        mockMvc.perform(get("/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Booking not found."));
    }

    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#getBookings
     */
    @Test
    void testGetBookings() throws Exception {

        List<BookingDTO> bookings = List.of(bookingDto);

        Mockito.when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.bookingDTOList[0].bookingId").value(1))
                .andExpect(jsonPath("$._embedded.bookingDTOList[0].provider").value(10))
                .andExpect(jsonPath("$._embedded.bookingDTOList[0].customer").value(20))
                .andExpect(jsonPath("$._embedded.bookingDTOList[0]._links.self.href").exists())
                .andExpect(jsonPath("$._links.self.href").exists());
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
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.provider").value(10))
                .andExpect(jsonPath("$.customer").value(20))
                .andExpect(jsonPath("$._links.self.href").exists());
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


    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#deleteBooking:success
     */
    @Test
    void testDeleteBooking_Success() throws Exception {

        Mockito.when(bookingService.deleteBooking(1)).thenReturn(1);

        mockMvc.perform(delete("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Booking deleted successfully."));
    }

    /**
     * Tests @Link com.basssoft.arms.booking.controller.BookingController#deleteBooking:bookingNotFound
     */
    @Test
    void testDeleteBooking_NotFound() throws Exception {

        Mockito.when(bookingService.deleteBooking(99)).thenReturn(-1);

        mockMvc.perform(delete("/bookings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Booking not found."));
    }

}
