package com.basssoft.arms.invoice;

import com.basssoft.arms.invoice.controller.InvoiceControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.invoice.service.InvoiceSvcTest;

/**
 * test suite for all invoice tests
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Suite
@SelectClasses({
        InvoiceSvcTest.class,
        InvoiceControllerTest.class
})
public class AllInvoiceTests {
}
