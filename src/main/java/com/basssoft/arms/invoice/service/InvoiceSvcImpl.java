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
     * @return boolean success (default true right now)
     */
    public InvoiceDTO createInvoice(InvoiceDTO account) {

        InvoiceDTO created = new InvoiceDTO();

        created.setInvoiceId(1);

        return created;
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
    public InvoiceDTO updateInvoice(InvoiceDTO account) {

        InvoiceDTO updated = new InvoiceDTO();
        updated.setInvoiceId(account.getInvoiceId());

        return updated;
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
