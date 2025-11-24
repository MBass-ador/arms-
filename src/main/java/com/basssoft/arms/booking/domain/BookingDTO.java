package com.basssoft.arms.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object
 * for Booking data

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private Integer bookingId;

    private Integer provider;
    private Integer customer;

    private String providerName;
    private String customerName;

    private BigDecimal hourlyRate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String locStreet;
    private String locCity;
    private String locState;
    private String locZipCode;

    private boolean completed;
    private BigDecimal overHours;
    private boolean paid;
}
