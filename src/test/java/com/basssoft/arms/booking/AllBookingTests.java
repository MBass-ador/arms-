package com.basssoft.arms.booking;

import com.basssoft.arms.booking.controller.BookingControllerTest;
import com.basssoft.arms.booking.service.BookingSvcTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * test suite for all booking-related tests

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Suite
@SelectClasses({
        BookingSvcTest.class,
        BookingControllerTest.class
})
public class AllBookingTests {

}
