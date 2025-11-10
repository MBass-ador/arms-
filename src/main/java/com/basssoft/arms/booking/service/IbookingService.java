package com.basssoft.arms.booking.service;

import com.basssoft.arms.booking.domain.BookingDTO;
import java.util.List;

/** * Booking Service Interface

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
public interface IbookingService {

    /**
     * Create new Booking
     *
     * @param booking BookingDTO
     * @return BookingDTO
     */
    public BookingDTO createBooking(BookingDTO booking);

    /**
     * Get Booking by ID
     *
     * @param bookingId int
     * @return BookingDTO
     */
    public BookingDTO getBooking(int bookingId);

    /**
     * Get all Bookings
     *
     * @return List<BookingDTO>
     */
    public List<BookingDTO> getAllBookings();

    /**
     * Update existing Booking
     *
     * @param booking BookingDTO
     * @return BookingDTO
     */
    public BookingDTO updateBooking(BookingDTO booking);

    /**
     * Delete Booking by ID
     *
     * @param bookingId int
     * @return int deletedId
     */
    public int deleteBooking(int bookingId);
}
