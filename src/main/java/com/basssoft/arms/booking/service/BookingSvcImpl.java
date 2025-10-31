package com.basssoft.arms.booking.service;

import com.basssoft.arms.booking.domain.Booking;
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
public class BookingSvcImpl {


    /**
     * Create new Booking

     * @param booking BookingDTO
     * @return boolean success (default true right now)
     */
    public boolean createBooking(BookingDTO booking) {

        return true;
    }


    /**
     * Get Booking by ID

     * @param bookingId int
     * @return BookingDTO (empty right now)
     */
    public BookingDTO getBooking(int bookingId) {

        return new BookingDTO();
    }


    /**
     * Get all Bookings

     * @return List<Booking> (empty right now)
     */
    public List<Booking> getAllBookings() {

        List<Booking> bookings = new ArrayList<>();

        return bookings;
    }


    /**
     * Update existing Booking

     * @param booking BookingDTO
     * @return boolean success (default true right now)
     */
    public boolean updateBooking(BookingDTO booking) {

        return true;
    }


    /**
     * Delete Booking by ID

     * @param bookingId int
     * @return boolean success (default true right now)
     */
    public boolean deleteBooking(int bookingId) {

        return true;
    }

}
