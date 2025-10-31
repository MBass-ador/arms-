package com.basssoft.arms.booking.service;

import com.basssoft.arms.booking.domain.Booking;
import com.basssoft.arms.booking.domain.BookingDTO;

import java.util.List;

public interface IbookingService {

    /**
     * Create new Booking
     *
     * @param booking BookingDTO
     * @return boolean success
     */
    public boolean createBooking(BookingDTO booking);

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
     * @return List<Booking>
     */
    public List<Booking> getAllBookings();

    /**
     * Update existing Booking
     *
     * @param booking BookingDTO
     * @return boolean success
     */
    public boolean updateBooking(BookingDTO booking);

    /**
     * Delete Booking by ID
     *
     * @param bookingId int
     * @return boolean success
     */
    public boolean deleteBooking(int bookingId);
}
