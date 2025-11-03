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
public class InvoiceSvcImpl implements IinvoiceService {



    /**
     * Create new Invoice

     * @param account InvoiceDTO
     * @return InvoiceDTO (passed in DTO right now)
     */
    public InvoiceDTO createInvoice(InvoiceDTO account) {

        return account;
    }


    /**
     * Get Invoice by ID

     * @param invoiceId int
     * @return InvoiceDTO
     */
    public InvoiceDTO getInvoice(int invoiceId) {

        InvoiceDTO created = new InvoiceDTO();
        created.setInvoiceId(invoiceId);
        return created;
    }


    /**
     * Get all Invoices

     * @return List<InvoiceDTO> invoices
     */
    public List<InvoiceDTO> getAllInvoices() {

        List<InvoiceDTO> invoices = new ArrayList<>();

        return invoices;
    }


    /**
     * Update existing Invoice

     * @param account InvoiceDTO
     * @return InvoiceDTO (same as passed in right now)
     */
    public InvoiceDTO updateInvoice(InvoiceDTO account) {

        return account;
    }


    /**
     * Delete Invoice by ID

     * @param invoiceId int
     * @return int deletedId (same as passed in right now)
     */
    public int deleteInvoice(int invoiceId) {

        return invoiceId;
    }

}
