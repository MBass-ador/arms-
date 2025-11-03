package com.basssoft.arms.booking.service;

import com.basssoft.arms.booking.domain.BookingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BookingSvcImpl}

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
public class BookingSvcTest {

    private BookingSvcImpl service;

    /**
     * sets up the test environment before each test
     */
    @BeforeEach
    public void setUp() {
        service = new BookingSvcImpl();
    }

    /**
     * Test method for {@link BookingSvcImpl#createBooking(BookingDTO)}.
     */
    @Test
    public void testCreateBooking() {
        BookingDTO bookingDTO = new BookingDTO();
        BookingDTO result = service.createBooking(bookingDTO);
        assertNotNull(result);
        assertNotNull(result.getBookingId());
    }


    /**
     * Test method for {@link BookingSvcImpl#getBooking(int)}.
     */
    @Test
    public void testGetBookingById() {
        BookingDTO booking = service.getBooking(1);
        assertNotNull(booking);
    }


    /**
     * Test method for {@link BookingSvcImpl#getAllBookings()}.
     */
    @Test
    public void testGetAllBookings() {
        List<BookingDTO> bookings = service.getAllBookings();
        assertNotNull(bookings);
    }


    /**
     * Test method for {@link BookingSvcImpl#updateBooking(BookingDTO)}.
     */
    @Test
    public void testUpdateBooking() {
        BookingDTO bookingDTO = new BookingDTO();
        BookingDTO result = service.updateBooking(bookingDTO);
        assertEquals(result, bookingDTO);
    }


    /**
     * Test method for {@link BookingSvcImpl#deleteBooking(int)}.
     */
    @Test
    public void testDeleteBooking() {
        int result = service.deleteBooking(1);
        assertEquals(1, result);
    }

}
