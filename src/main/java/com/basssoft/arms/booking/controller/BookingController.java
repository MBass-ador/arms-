package com.basssoft.arms.booking.controller;

import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.service.IbookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Booking Controller class
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private IbookingService bookingService;


    /**
     * Create new Booking
     *
     * @param bookingDTO BookingDTO
     * @return ResponseEntity with created BookingDTO or error message
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO) {

        try {
            // Call service to create booking
            BookingDTO createdBooking = bookingService.createBooking(bookingDTO);

            // Return status: 201 and DTO of created booking
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);

        } catch (IllegalArgumentException ex) {
            // Handle bad input
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // Handle other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking creation failed.");
        }
    }




    /**
     * Update existing Booking
     *
     * @param id         booking ID
     * @param bookingDTO BookingDTO
     * @return ResponseEntity with updated BookingDTO or error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable int id, @RequestBody BookingDTO bookingDTO) {

        try {
            // set ID from path variable
            bookingDTO.setBookingId(id);

            // call service
            BookingDTO updatedBooking = bookingService.updateBooking(bookingDTO);

            if (updatedBooking == null) {
                // booking not found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }

            // return updated booking, status: 200
            return ResponseEntity.ok(updatedBooking);

        } catch (IllegalArgumentException ex) {
            // handle bad input
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // handle other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking update failed.");
        }
    }



}
