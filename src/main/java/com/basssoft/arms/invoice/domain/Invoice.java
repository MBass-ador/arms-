package com.basssoft.arms.invoice.domain;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.Booking;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Invoice
 * domain entity class

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Entity
@Data
@Table(name = "invoices")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Account provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Account customer;

    // all unpaid bookings between
    // this provider and this customer
    @Transient
    @ToString.Exclude
    private List<Booking> bookings = new ArrayList<>();

    @Column(precision = 20, scale = 2, nullable = false)
    private BigDecimal totalAmountDue = BigDecimal.ZERO;

    // timestamp of last attempt to collect
    @Column
    private LocalDateTime lastContacted;


    /* constructor to create Invoice with provider and customer
     * initializes bookings as empty list and totalAmountDue as zero
     */
    public Invoice(Account provider, Account customer) {
        this.provider = provider;
        this.customer = customer;
        this.bookings = new ArrayList<>();
        this.totalAmountDue = java.math.BigDecimal.ZERO;
        this.lastContacted = null;
    }



}
