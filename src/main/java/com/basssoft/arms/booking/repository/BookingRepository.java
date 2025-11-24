package com.basssoft.arms.booking.repository;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for Booking entity
 *
 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
public interface BookingRepository extends JpaRepository<Booking,Integer> {

    // used by Booking Service to retrieve all bookings for a customer
    List<Booking> findByCustomer_AccountId(int accountId);

    // used by Invoice Service Factory to generate Invoices
    // finds unpaid/completed bookings between provider/customer
    List<Booking> findByProviderAndCustomerAndCompletedTrueAndPaidFalse(Account provider, Account customer);
}
