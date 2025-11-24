package com.basssoft.arms;

import com.basssoft.arms.account.AllAccountTests;
import com.basssoft.arms.booking.AllBookingTests;
import com.basssoft.arms.invoice.AllInvoiceTests;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * aggregates per service test suites
 * for the arms application

 * @author Matthew Bass
 * @version 2.0
 */
@Suite
@SelectClasses({
        AllBookingTests.class,
        AllAccountTests.class,
        AllInvoiceTests.class
})
class ArmsApplicationTests {

    @Test
    void contextLoads() {
    }


}
