package com.basssoft.arms.invoice.service;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Implementation
 * for Invoice CRUD operations

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Service
public class InvoiceSvcImpl {



    /**
     * Create new Invoice

     * @param account InvoiceDTO
     * @return boolean success (default true right now)
     */
    public boolean createInvoice(InvoiceDTO account) {

        return true;
    }


    /**
     * Get Invoice by ID

     * @param invoiceId int
     * @return InvoiceDTO (empty right now)
     */
    public InvoiceDTO getInvoice(int invoiceId) {

        return new InvoiceDTO();
    }


    /**
     * Get all Invoices

     * @return List<Invoice>
     */
    public List<InvoiceDTO> getAllInvoices() {

        List<InvoiceDTO> invoices = new ArrayList<>();

        return invoices;
    }


    /**
     * Update existing Invoice

     * @param account InvoiceDTO
     * @return boolean success
     */
    public boolean updateInvoice(InvoiceDTO account) {

        return true;
    }


    /**
     * Delete Invoice by ID

     * @param invoiceId int
     * @return boolean success
     */
    public boolean deleteInvoice(int invoiceId) {
        return true;
    }

}
