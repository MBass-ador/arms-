package com.basssoft.arms.invoice.domain;

import com.basssoft.arms.booking.domain.Booking;
import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object
 * for Invoice data

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Data
public class InvoiceDTO {

    private int invoiceId;

    private String providerId;
    private String customerId;

    private List<Booking> bookings;

    private float totalAmountDue;

    private String lastContacted;
}
