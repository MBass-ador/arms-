package com.basssoft.arms.invoice.domain;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.Booking;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

/**
 * Invoice
 * domain entity class

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Invoice {

    @Id
    private int invoiceId;

    @OneToOne
    private Account provider;

    @OneToOne
    private Account customer;

    // all unpaid bookings between
    // this provider and this customer
    private List<Booking> bookings;

    private float totalAmountDue;

    // timestamp of last attempt to collect
    private LocalDateTime lastContacted;
}
