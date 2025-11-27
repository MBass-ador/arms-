package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 *  tests InvoiceWebController class
 *
 * arms application
 * @author Matthew Bass
 * @version 4.0
 */
@Transactional
public class InvoiceWebControllerTest {

    private IinvoiceService invoiceService;
    private InvoiceWebController controller;

    @BeforeEach
    void setUp() {
        invoiceService = mock(IinvoiceService.class);
        controller = new InvoiceWebController(invoiceService);
    }



    /**
     *  tests viewInvoice()
     */
    @Test
    void testViewInvoice() {

        int invoiceId = 200;
        int customerId = 2;

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setInvoiceId(invoiceId);
        invoice.setCustomerId(customerId);

        when(invoiceService.getInvoice(invoiceId)).thenReturn(invoice);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.viewInvoice(invoiceId, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/" + customerId, view);
        verify(redirectAttributes).addFlashAttribute("selectedInvoice", invoice);
        verify(redirectAttributes).addFlashAttribute("accountId", customerId);
        verify(redirectAttributes).addFlashAttribute("invoiceMode", "view");
    }



    /** tests getCustomerInvoices()
     */
    @Test
    void testGetCustomerInvoices() {

        int customerId = 1;

        InvoiceDTO inv1 = new InvoiceDTO();
        inv1.setInvoiceId(101);
        inv1.setCustomerId(customerId);

        InvoiceDTO inv2 = new InvoiceDTO();
        inv2.setInvoiceId(102);
        inv2.setCustomerId(customerId);

        InvoiceDTO inv3 = new InvoiceDTO();
        inv3.setInvoiceId(103);
        inv3.setCustomerId(customerId);

        List<InvoiceDTO> mockInvoices = List.of(inv1, inv2, inv3);

        when(invoiceService.getCustomerInvoices(customerId)).thenReturn(mockInvoices);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.getCustomerInvoices(customerId, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/" + customerId, view);
        verify(redirectAttributes).addFlashAttribute("invoices", mockInvoices);
        verify(redirectAttributes).addFlashAttribute("accountId", customerId);
        verify(redirectAttributes).addFlashAttribute("invoiceMode", "default");
    }



    /**
     *  tests cancelInvoice()
     */
    @Test
    void testCancelInvoice() {

        int accountId = 5;

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.cancelInvoice(accountId, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/" + accountId, view);
        verify(redirectAttributes).addFlashAttribute("invoiceMode", "default");
        verify(redirectAttributes).addFlashAttribute("selectedInvoice", null);
        verify(redirectAttributes).addFlashAttribute("accountId", accountId);
    }



    /**
     *  tests payInvoice()
     */
    @Test
    void testPayInvoice() {

        int accountId = 7;

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.payInvoice(accountId, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/" + accountId, view);
        verify(redirectAttributes).addFlashAttribute("invoiceMode", "pay");
        verify(redirectAttributes).addFlashAttribute("accountId", accountId);
    }



    /**
     *  tests messageInvoice()
     */
    @Test
    void testMessageInvoice() {
        int accountId = 9;
        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = controller.messageInvoice(accountId, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/" + accountId, view);
        verify(redirectAttributes).addFlashAttribute("invoiceMode", "message");
        verify(redirectAttributes).addFlashAttribute("accountId", accountId);
    }

}
