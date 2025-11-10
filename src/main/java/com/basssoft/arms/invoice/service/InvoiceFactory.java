package com.basssoft.arms.invoice.service;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.Booking;
import com.basssoft.arms.booking.repository.BookingRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;


/**
 * Factory Service
 * for Invoice creation
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class InvoiceFactory {

    private final BookingRepository bookingRepository;

    /**
     * Create Invoice from unpaid Bookings
     *
     * @param provider Account
     * @param customer Account
     * @return Invoice
     */
    public Invoice createInvoice (Account provider, Account customer) {

        // get all unpaid bookings for provider/customer
        List<Booking> unpaidBookings = bookingRepository
                .findByProviderAndCustomerAndCompletedTrueAndPaidFalse(provider, customer);

        // create invoice
        Invoice invoice = new Invoice();

        // set fields
        invoice.setProvider(provider);
        invoice.setCustomer(customer);
        invoice.setBookings(unpaidBookings);

        // calculate total amount due
        BigDecimal totalAmountDue = unpaidBookings.stream()
                .map(this::_lineAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        invoice.setTotalAmountDue(totalAmountDue);

        return invoice;
    }


    /** helper method
     *  calculates line amount for a Booking
     *
     * @param b Booking
     * @return BigDecimal line amount
     */
    private BigDecimal _lineAmount(Booking b) {

        // handle no booking
        if (b == null) {
            return BigDecimal.ZERO;
        }
        // treat null as zero
        BigDecimal hourlyRate = b.getHourlyRate() == null ? BigDecimal.ZERO : b.getHourlyRate();
        BigDecimal overHours = b.getOverHours() == null ? BigDecimal.ZERO : b.getOverHours();

        // duration in milliseconds -> hours BigDecimal
        Duration dur;

        try {
            // calculate duration
            dur = Duration.between(b.getStartTime(), b.getEndTime());

        } catch (Exception e) {
            // on error, treat as zero duration
            dur = Duration.ZERO;
        }
        // convert duration to hours
        BigDecimal hours = BigDecimal.valueOf(dur.toMillis())
                .divide(BigDecimal.valueOf(3_600_000L), 6, RoundingMode.HALF_UP);

        // ensure hours not negative
        if (hours.compareTo(BigDecimal.ZERO) < 0) {
            hours = BigDecimal.ZERO;
        }
        // add overHours (can be negative)
        BigDecimal totalHours = hours.add(overHours);

        // ensure totalHours not negative
        if (totalHours.compareTo(BigDecimal.ZERO) < 0) {
            totalHours = BigDecimal.ZERO;
        }
        // multiply by hourly rate
        return hourlyRate.multiply(totalHours);
    }

}
