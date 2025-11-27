package com.basssoft.arms.booking.repository;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository interface for Booking entity
 *
 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
public interface BookingRepository extends JpaRepository<Booking,Integer> {

    /**
     *  find Bookings by Provider Account ID
     *
     * @param accountId int
     * @return List<Booking>
     */
    List<Booking> findByCustomer_AccountId(int accountId);


    /**
     *  find Bookings by Provider ID, Customer ID, Completed status, and Paid status
     *
     * @param providerId int
     * @param customerId int
     * @return List<Booking>
     */
    List<Booking> findByProvider_AccountIdAndCustomer_AccountIdAndCompletedTrueAndPaidFalse(int providerId, int customerId);




    /**
     *  find distinct Customer Account IDs by Provider ID, Completed status, and Paid status
     *
     * @param providerId int
     * @return List<Integer>
     */
    @Query("select distinct b.customer.accountId from Booking b where b.provider.accountId = :providerId and b.completed = true and b.paid = false")
    List<Integer> findDistinctCustomer_AccountIdByProvider_AccountIdAndCompletedTrueAndPaidFalse(int providerId);


}
