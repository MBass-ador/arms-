package com.basssoft.arms.gateway;


import com.basssoft.arms.account.controller.AccountController;
import com.basssoft.arms.booking.controller.BookingController;
import com.basssoft.arms.invoice.controller.InvoiceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Restful API endpoint class

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@RestController
@RequestMapping("/api")
public class ArmsAPIEndpoint {

    @Autowired
    private AccountController accountController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private InvoiceController invoiceController;
}
