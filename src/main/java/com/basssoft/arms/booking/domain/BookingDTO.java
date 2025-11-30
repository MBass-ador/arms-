package com.basssoft.arms.booking.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFilter;
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
@JsonFilter("omitNames")
public class BookingDTO {

    private Integer bookingId;

    @NotNull
    private Integer provider;

    @NotNull
    private Integer customer;

    @Size(min = 8, max = 20)
    private String providerName;

    @Size(min = 8, max = 20)
    private String customerName;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal hourlyRate;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    @Size(max = 50)
    private String locStreet;

    @NotBlank
    @Size(max = 24)
    private String locCity;

    @NotBlank
    @Size(max = 2)
    private String locState;

    @NotBlank
    @Size(max = 10)
    private String locZipCode;

    private boolean completed;
    private BigDecimal overHours;
    private boolean paid;
}
