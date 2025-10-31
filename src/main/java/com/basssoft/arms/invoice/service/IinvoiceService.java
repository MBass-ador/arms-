package com.basssoft.arms.invoice.service;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import java.util.List;

/**
 * Invoice Service Interface
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
public interface IinvoiceService {

    /**
     * Create new Invoice
     *
     * @param account InvoiceDTO
     * @return boolean success
     */
    public boolean createInvoice(InvoiceDTO account);

    /**
     * Get Invoice by ID
     *
     * @param invoiceId int
     * @return InvoiceDTO
     */
    public InvoiceDTO getInvoice(int invoiceId);

    /**
     * Get all Invoices
     *
     * @return List<Invoice>
     */
    public List<InvoiceDTO> getAllInvoices();

    /**
     * Update existing Invoice
     *
     * @param account InvoiceDTO
     * @return boolean success
     */
    public boolean updateInvoice(InvoiceDTO account);

    /**
     * Delete Invoice by ID
     *
     * @param invoiceId int
     * @return boolean success
     */
    public boolean deleteInvoice(int invoiceId);
}
