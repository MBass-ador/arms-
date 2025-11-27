package com.basssoft.arms.invoice.service;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import java.util.List;

/**
 * Invoice Service Interface
 *
 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
public interface IinvoiceService {



    /**
     * Get Invoice by ID

     * @param invoiceId int
     * @return InvoiceDTO
     */
    public InvoiceDTO getInvoice(int invoiceId);

    /**
     * Get all Invoices

     * @param customerId int - the customer ID to get invoices for
     * @return List<InvoiceDTO>
     */
    public List<InvoiceDTO> getCustomerInvoices(int customerId);

}
