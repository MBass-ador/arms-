package com.basssoft.arms.booking.controller;

import com.basssoft.arms.booking.domain.BookingDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * Assembles Booking models (links)
 * for HATEOAS responses

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Component
public class BookingModelAssembler
        implements RepresentationModelAssembler<BookingDTO, EntityModel<BookingDTO>>  {

    @Override
    public EntityModel<BookingDTO> toModel(BookingDTO booking) {

        return EntityModel.of(booking,
                linkTo(methodOn(BookingController.class).getBooking(booking.getBookingId())).withSelfRel(),
                linkTo(methodOn(BookingController.class).getBookings()).withRel("bookings")
        );
    }

}
