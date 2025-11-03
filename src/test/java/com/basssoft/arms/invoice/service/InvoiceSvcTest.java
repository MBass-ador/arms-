package com.basssoft.arms.invoice.service;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InvoiceSvcImpl}

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
public class InvoiceSvcTest {


    private InvoiceSvcImpl service;

    /**
     * Setup method initializes service before each test
     */
    @BeforeEach
    public void setUp() {
        service = new InvoiceSvcImpl();
    }


    /**
     * Test method for {@link InvoiceSvcImpl#createInvoice(InvoiceDTO)}.
     */
    @Test
    public void testCreateInvoice() {

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        InvoiceDTO result = service.createInvoice(invoiceDTO);
        assertNotNull(result);
        assertNotNull(result.getInvoiceId());
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#getInvoice(int)}.
     */
    @Test
    public void testGetInvoiceById() {

        InvoiceDTO invoice = service.getInvoice(1);
        assertNotNull(invoice);
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#getAllInvoices()}.
     */
    @Test
    public void testGetAllInvoices() {

        List<InvoiceDTO> invoices = service.getAllInvoices();
        assertNotNull(invoices);
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#updateInvoice(InvoiceDTO)}.
     */
    @Test
    public void testUpdateInvoice() {

        InvoiceDTO invoiceDTO = new InvoiceDTO();
        InvoiceDTO result = service.updateInvoice(invoiceDTO);
        assertEquals(result, invoiceDTO);
    }
    
    /**
     * Test method for {@link InvoiceSvcImpl#deleteInvoice(int)}.
     */
    @Test
    public void testDeleteInvoice() {

        int result = service.deleteInvoice(1);
        assertEquals(1, result);
    }



}
