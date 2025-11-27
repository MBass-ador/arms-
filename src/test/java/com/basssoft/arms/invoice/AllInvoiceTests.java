package com.basssoft.arms.invoice;

import com.basssoft.arms.invoice.controller.InvoiceFactoryControllerTest;
import com.basssoft.arms.invoice.controller.InvoiceWebControllerTest;
import com.basssoft.arms.invoice.service.InvoiceFactoryTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.invoice.service.InvoiceSvcTest;

/**
 * test suite for all invoice tests
 *
 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Suite
@SelectClasses({
        InvoiceSvcTest.class,
        InvoiceWebControllerTest.class,
        InvoiceFactoryTest.class,
        InvoiceFactoryControllerTest.class,
})
public class AllInvoiceTests {
}
