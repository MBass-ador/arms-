package com.basssoft.arms.booking.domain;

import com.basssoft.arms.account.domain.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
  * Booking
  * domain entity class

  * arms application
  * @author Matthew Bass
  * @version 1.0
  */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
public class Booking {

    @Id
    private int bookingId;

    @OneToOne
    private Account provider;

    @OneToOne
    private Account customer;

    private float hourlyRate;

    // start-end times
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // location
    private String locStreet;
    private String locCity;
    private String locState;
    private String locZipCode;

    // was service delivered?
    private boolean completed;

    // time used over booking (or under if negative)
    // in hours (ex:  1.5 = one hour 30 minutes over
    //           ex: -0.5 = half hour under)
    private float overHours;

    // has payment been received?
    private boolean paid;
}
