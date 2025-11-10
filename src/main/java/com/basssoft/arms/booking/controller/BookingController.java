package com.basssoft.arms.booking.controller;

import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.service.IbookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Booking Controller class

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private IbookingService bookingService;

    @Autowired
    private BookingModelAssembler assembler;

    /**
     * Create new Booking
     *
     * @param bookingDTO BookingDTO
     * @return ResponseEntity with created BookingDTO and or status code
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingDTO bookingDTO) {

        try {
            // call service to create booking
            BookingDTO createdBooking = bookingService.createBooking(bookingDTO);

            // return HATEOAS wrapped DTO / status: 201
            return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(createdBooking));

        } catch (IllegalArgumentException ex) {
            // handle bad input status: 400
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // handle other errors status: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking creation failed.");
        }
    }


    /**
     * Get Booking by ID
     *
     * @param id booking ID
     * @return ResponseEntity with BookingDTO and or status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable int id) {

        try {
            // call service / get booking
            BookingDTO booking = bookingService.getBooking(id);

            if (booking == null) {
                // when booking not found status: 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }

            // return link wrapped booking, status: 200
            return ResponseEntity.ok(assembler.toModel(booking));

        } catch (IllegalArgumentException iae) {
            // handle bad request status: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors status: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking retrieval failed." + ex.getMessage());
        }
    }


    /**
     * Get all Bookings
     *
     * @return ResponseEntity with list of BookingDTOs or error message
     */
    @GetMapping
    public ResponseEntity<?> getBookings() {

        try {
            // call service / get bookings
            List<BookingDTO> bookings = bookingService.getAllBookings();

            // wrap each booking with HATEOAS links
            List<EntityModel<BookingDTO>> bookingModels = bookings.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            // create collection of link wrapped DTOs
            CollectionModel<EntityModel<BookingDTO>> collectionModel = CollectionModel.of(
                    bookingModels,
                    linkTo(methodOn(BookingController.class).getBookings()).withSelfRel()
            );
            // return collection, status:200
            return ResponseEntity.ok(collectionModel);

        } catch (IllegalArgumentException iae) {
            // handle bad request status: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        }catch (Exception ex) {
            // handle other errors status: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking retrieval failed.");
        }
    }


    /**
     * Update existing Booking
     *
     * @param id         int
     * @param bookingDTO BookingDTO
     * @return ResponseEntity with updated BookingDTO and or status code
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable int id, @RequestBody BookingDTO bookingDTO) {

        try {
            // set ID from path variable
            bookingDTO.setBookingId(id);

            // call service
            BookingDTO updatedBooking = bookingService.updateBooking(bookingDTO);

            if (updatedBooking == null) {
                // booking not found status: 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }

            // return updated booking wrapped in HATEOAS links, status: 200
            return ResponseEntity.ok(assembler.toModel(updatedBooking));

        } catch (IllegalArgumentException ex) {
            // handle bad input status: 400
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // handle other errors status: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Booking update failed.");
        }
    }



    /**
     * Delete Booking by ID
     *
     * @param id booking ID
     * @return ResponseEntity with status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable int id) {

        try {
            // call service / delete booking
            int deletedId = bookingService.deleteBooking(id);

            if (deletedId == id) {
                // success status: 200
                return ResponseEntity.ok("Booking deleted successfully.");

            } else {
                // booking not found status: 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found.");
            }
        } catch (IllegalArgumentException iae) {
            // handle bad request status: 400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // handle other errors status: 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking deletion failed." + ex.getMessage());
        }
    }


}
