package com.basssoft.arms.booking;

import com.basssoft.arms.booking.service.BookingSvcTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        BookingSvcTest.class
})
public class AllBookingTests {

}
