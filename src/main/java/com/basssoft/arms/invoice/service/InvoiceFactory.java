package com.basssoft.arms.invoice.service;

import com.basssoft.arms.booking.domain.Booking;
import com.basssoft.arms.booking.repository.BookingRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Factory Service
 * for Invoice creation

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class InvoiceFactory {

    private final BookingRepository bookingRepo;

    Logger exceptionLogger = LoggerFactory.getLogger("com.basssoft.arms.exception");


    /**
     * Create Invoice from unpaid Bookings
     *
     * @param providerId int
     * @param customerId int
     * @return Invoice Mono
     */
    public Mono<Invoice> generateInvoice (int providerId, int customerId) {

        return Mono.fromCallable(() ->
                        bookingRepo.findByProvider_AccountIdAndCustomer_AccountIdAndCompletedTrueAndPaidFalse(providerId, customerId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(bookings -> {
                    if (bookings == null || bookings.isEmpty()) {
                        return Mono.empty();
                    }

            Invoice invoice = new Invoice();
            invoice.setProvider(bookings.get(0).getProvider());
            invoice.setCustomer(bookings.get(0).getCustomer());
            invoice.setBookings(bookings);

            BigDecimal totalAmountDue = bookings.stream()
                    .map(this::_lineAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            invoice.setTotalAmountDue(totalAmountDue);
            return Mono.just(invoice);

        })
        .doOnError(ex -> exceptionLogger.error("Exception in generateInvoice: providerId={}, customerId={}",
                providerId, customerId, ex));
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


    /**
     * Get unpaid Customer Account IDs for Provider
     * calls custom BookingRepository query
     *
     * @param providerId int
     * @return Flux<Integer> of Customer Account IDs
     */
    public Flux<Integer> unpaidCustomerAccounts(int providerId) {

        return Mono.fromCallable(() -> bookingRepo.findDistinctCustomer_AccountIdByProvider_AccountIdAndCompletedTrueAndPaidFalse(providerId))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }


}
