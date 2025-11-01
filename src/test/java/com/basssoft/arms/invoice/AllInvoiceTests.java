package com.basssoft.arms.invoice;

import com.basssoft.arms.invoice.controller.InvoiceControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.invoice.service.InvoiceSvcTest;

@Suite
@SelectClasses({
        InvoiceSvcTest.class,
        InvoiceControllerTest.class
})
public class AllInvoiceTests {
}
