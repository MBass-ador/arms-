package com.basssoft.arms.gateway;

import com.basssoft.arms.account.controller.AccountController;
import com.basssoft.arms.booking.controller.BookingController;
import com.basssoft.arms.invoice.controller.InvoiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Central REST API endpoint
 * for arms application.

 * aggregates controllers for: account, booking, and invoice services
 * All endpoints accessible under `/api` path.

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@RestController
@RequestMapping("/api")
public class ArmsAPIEndpoint {

    /**
     * handles account API requests.
     */
    @Autowired
    private AccountController accountController;

    /**
     * handles booking API requests.
     */
    @Autowired
    private BookingController bookingController;

    /**
     * handles invoice API requests.
     */
    @Autowired
    private InvoiceController invoiceController;
}
