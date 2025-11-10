package com.basssoft.arms.booking.domain;

import com.basssoft.arms.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
  * Booking
  * domain entity class

  * arms application
  * @author Matthew Bass
  * @version 2.0
  */
@Getter
@Setter
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private Account provider;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Account customer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 40)
    private String locStreet;

    @Column(nullable = false, length = 20)
    private String locCity;

    @Column(nullable = false, length = 20)
    private String locState;

    @Column(nullable = false, length = 10)
    private String locZipCode;

    // was service delivered?
    @Column(nullable = false)
    private boolean completed;

    // time used over booking (or under if negative)
    // in hours (ex:  1.5 = one hour 30 minutes over
    //           ex: -0.5 = half hour under)
    @Column(nullable = false, precision = 7, scale = 3)
    private BigDecimal overHours;

    // has payment been received?
    @Column(nullable = false)
    private boolean paid;
}
