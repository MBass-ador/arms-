package com.basssoft.arms.invoice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object
 * for Invoice data

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {

    private Integer invoiceId;

    private Integer providerId;
    private Integer customerId;

    private List<Integer> bookingIds;

    private BigDecimal totalAmountDue;

    private LocalDateTime lastContacted;
}
