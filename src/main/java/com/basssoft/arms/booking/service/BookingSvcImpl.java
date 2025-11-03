package com.basssoft.arms.booking.service;

import com.basssoft.arms.booking.domain.BookingDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/** * Booking Service Implementation

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Service
public class BookingSvcImpl implements IbookingService {


    /**
     * Create new Booking

     * @param booking BookingDTO
     * @return BookingDTO (same as input right now)
     */
    public BookingDTO createBooking(BookingDTO booking) {

        return booking;
    }


    /**
     * Get Booking by ID

     * @param bookingId int
     * @return BookingDTO
     */
    public BookingDTO getBooking(int bookingId) {

        BookingDTO created = new BookingDTO();
        created.setBookingId(bookingId);
        return created;
    }


    /**
     * Get all Bookings

     * @return List<BookingDTO> (empty right now)
     */
    public List<BookingDTO> getAllBookings() {

        List<BookingDTO> bookings = new ArrayList<>();

        return bookings;
    }


    /**
     * Update existing Booking

     * @param booking BookingDTO
     * @return BookingDTO (same as input right now)
     */
    public BookingDTO updateBooking(BookingDTO booking) {

        return booking;
    }


    /**
     * Delete Booking by ID

     * @param bookingId int
     * @return int deletedId
     */
    public int deleteBooking(int bookingId) {

        return bookingId;
    }

}
