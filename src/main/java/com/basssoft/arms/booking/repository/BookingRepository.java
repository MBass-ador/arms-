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

    // used by Invoice Service
    // to find unpaid completed bookings between provider/customer
    List<Booking> findByProviderAndCustomerAndCompletedTrueAndPaidFalse(Account provider, Account customer);
}
