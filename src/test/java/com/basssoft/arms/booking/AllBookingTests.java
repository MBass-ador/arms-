package com.basssoft.arms.booking;

import com.basssoft.arms.booking.controller.BookingWebControllerTest;
import com.basssoft.arms.booking.service.BookingSvcTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * test suite for all booking-related tests

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Suite
@SelectClasses({
        BookingSvcTest.class,
        BookingWebControllerTest.class
})
public class AllBookingTests {

}
