package com.basssoft.arms.invoice.service;


import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.booking.domain.Booking;
import com.basssoft.arms.booking.repository.BookingRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import com.basssoft.arms.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *  Test class for InvoiceFactory
 *
 * arms application
 * @author Matthew Bass
 * @version 5.0
 */
@SpringBootTest
@Transactional
public class InvoiceFactoryTest {

    @Autowired
    private InvoiceFactory invoiceFactory;

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private InvoiceRepository invoiceRepository;


    /**
     * Test method for generateInvoice
     */
    @Test
    public void testGenerateInvoice() {

        // test data: provider "derek", customer "alice" from import.sql
        Account provider = accountRepo.findAll().stream()
                .filter(a -> "derek".equals(a.getScreenName()))
                .findFirst().orElseThrow();
        Account customer = accountRepo.findAll().stream()
                .filter(a -> "alice".equals(a.getScreenName()))
                .findFirst().orElseThrow();

        int providerId = provider.getAccountId();
        int customerId = customer.getAccountId();

        // calc expected total
        List<Booking> bookings = bookingRepo.findByProvider_AccountIdAndCustomer_AccountIdAndCompletedTrueAndPaidFalse(providerId, customerId);
        BigDecimal expectedTotal = bookings.stream()
                .map(b -> {
                    // reproduce _lineAmount logic
                    BigDecimal hourlyRate = b.getHourlyRate() == null ? BigDecimal.ZERO : b.getHourlyRate();
                    BigDecimal overHours = b.getOverHours() == null ? BigDecimal.ZERO : b.getOverHours();
                    Duration dur;
                    try {
                        dur = Duration.between(b.getStartTime(), b.getEndTime());
                    } catch (Exception e) {
                        dur = Duration.ZERO;
                    }
                    BigDecimal hours = BigDecimal.valueOf(dur.toMillis())
                            .divide(BigDecimal.valueOf(3_600_000L), 6, RoundingMode.HALF_UP);
                    if (hours.compareTo(BigDecimal.ZERO) < 0) hours = BigDecimal.ZERO;
                    BigDecimal totalHours = hours.add(overHours);
                    if (totalHours.compareTo(BigDecimal.ZERO) < 0) totalHours = BigDecimal.ZERO;
                    return hourlyRate.multiply(totalHours);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);


        StepVerifier.create(invoiceFactory.generateInvoice(providerId, customerId))
                .assertNext(invoice -> {
                    assertNotNull(invoice);
                    assertEquals(provider.getAccountId(), invoice.getProvider().getAccountId());
                    assertEquals(customer.getAccountId(), invoice.getCustomer().getAccountId());
                    assertEquals(expectedTotal, invoice.getTotalAmountDue());
                    assertEquals(bookings.size(), invoice.getBookings().size());
                })
                .verifyComplete();
    }



    /**
     * Test unpaidCustomerAccounts method
     */
    @Test
    public void testUnpaidCustomerAccounts() {

        // provider "derek" from import.sql
        Account provider = accountRepo.findAll().stream()
                .filter(a -> "derek".equals(a.getScreenName()))
                .findFirst().orElseThrow();

        int providerId = provider.getAccountId();

        // get expected customerIds directly from repo
        List<Integer> expectedIds = bookingRepo.findDistinctCustomer_AccountIdByProvider_AccountIdAndCompletedTrueAndPaidFalse(providerId);

        // verify factory method reactively
        StepVerifier.create(invoiceFactory.unpaidCustomerAccounts(providerId))
                .expectNextSequence(expectedIds)
                .verifyComplete();
    }


}
