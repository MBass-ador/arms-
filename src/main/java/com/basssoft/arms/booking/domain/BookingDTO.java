package com.basssoft.arms.booking.domain;

import com.basssoft.arms.account.domain.Account;
import lombok.Data;

/**
 * Data Transfer Object
 * for Booking data

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Data
public class BookingDTO {

    private Account provider;
    private Account customer;

    private float hourlyRate;
    private String startTime;
    private String endTime;

    private String locStreet;
    private String locCity;
    private String locState;
    private String locZipCode;

    private boolean completed;
    private float overHours;
}
