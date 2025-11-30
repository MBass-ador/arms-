package com.basssoft.arms.booking.domain;

import com.basssoft.arms.account.domain.Account;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Column(name = "booking_id", nullable = false)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    @NotNull
    private Account provider;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    private Account customer;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal hourlyRate;

    @Column(name = "start_time", nullable = false)
    @NotNull
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull
    private LocalDateTime endTime;

    @Column(name = "loc_street", nullable = false, length = 40)
    @NotBlank
    @Size(max = 50)
    private String locStreet;

    @Column(name = "loc_city", nullable = false, length = 20)
    @NotBlank
    @Size(max = 24)
    private String locCity;

    @Column(name = "loc_state", nullable = false, length = 20)
    @NotBlank
    @Size(max = 2)
    private String locState;

    @Column(name = "loc_zip_code", nullable = false, length = 10)
    @NotBlank
    @Size(max = 10)
    private String locZipCode;

    // was service delivered?
    @Column(nullable = false)
    private boolean completed;

    // time used over booking (or under if negative)
    // in hours (ex:  1.5 = one hour 30 minutes over
    //           ex: -0.5 = half hour under)
    @Column(name = "over_hours", nullable = false, precision = 7, scale = 3)
    private BigDecimal overHours;

    // has payment been received?
    @Column(nullable = false)
    private boolean paid;
}
