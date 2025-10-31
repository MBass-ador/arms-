package com.basssoft.arms.invoice;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.invoice.service.InvoiceSvcTest;

@Suite
@SelectClasses({
        InvoiceSvcTest.class
})
public class AllInvoiceTests {
}
