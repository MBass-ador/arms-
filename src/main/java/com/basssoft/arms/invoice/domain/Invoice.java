package com.basssoft.arms.invoice.domain;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.Booking;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Column(name = "invoice_id", nullable = false)
    private Integer invoiceId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", referencedColumnName = "account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Account provider;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private Account customer;

    // all unpaid bookings between
    // this provider and this customer
    @NotEmpty
    @ManyToMany
    @JoinTable(
            name = "invoice_bookings",
            joinColumns = @JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "booking_id")
    )
    private List<Booking> bookings = new ArrayList<>();

    @NotNull
    @DecimalMin("0.01")
    @Column(name = "total_amount_due", precision = 20, scale = 2, nullable = false)
    private BigDecimal totalAmountDue = BigDecimal.ZERO;

    // timestamp of last attempt to collect
    @Column(name = "last_contacted")
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
