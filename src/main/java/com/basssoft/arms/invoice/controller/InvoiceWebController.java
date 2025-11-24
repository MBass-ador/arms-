package com.basssoft.arms.invoice.controller;


import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

/**
 *  InvoiceWebController class
 *  for thymeleaf server-side rendered HTML views
 *
 * arms application
 * @author Matthew Bass
 * @version 4.0
 */
@Controller
public class InvoiceWebController {

    // dependencies
    private final IinvoiceService invoiceService;

    /**
     * Constructor for InvoiceWebController
     * injects dependencies
     *
     * @param invoiceService IinvoiceService
     */
    public InvoiceWebController(IinvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }


    /**
     * View invoice by ID
     *
     * @param invoiceId int
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return Thymeleaf fragment name
     */
    @GetMapping("/invoices/view/{id}")
    public String viewInvoice(@PathVariable("id") int invoiceId, Model model, RedirectAttributes redirectAttributes) {

        try {
            // get invoice via service
            InvoiceDTO invoice = invoiceService.getInvoice(invoiceId);

            // add to model
            if (invoice != null) {
                redirectAttributes.addFlashAttribute("selectedInvoice", invoice);
                redirectAttributes.addFlashAttribute("accountId", invoice.getCustomerId());
                // set mode to view
                redirectAttributes.addFlashAttribute("invoiceMode", "view");
            }
            // return to view
            return "redirect:/ArmsSPA/" + invoice.getCustomerId();

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to load invoice.");
            return "error";
        }
    }



  /**
     * Get all invoices for a customer
     *
     * @param customerId int
     * @param model Model
   *   @param redirectAttributes RedirectAttributes
   *
     * @return Thymeleaf fragment name
     */
    @GetMapping(value = "/invoices", produces = "text/html")
    public String getCustomerInvoices(@RequestParam int customerId, Model model, RedirectAttributes redirectAttributes) {

        try {
            // get all invoices via service
            List<InvoiceDTO> invoices = invoiceService.getCustomerInvoices(customerId);

            // add to model
            redirectAttributes.addFlashAttribute("invoices", invoices);
            redirectAttributes.addFlashAttribute("accountId", customerId);
            redirectAttributes.addFlashAttribute("invoiceMode", "default");

            // return to view
            return "redirect:/ArmsSPA/" + customerId;

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to load invoices.");
            return "error";
        }
    }

// Button handler methods

    /**
     *  cancel invoice subfunction
     *  reloads page in default view
     *
     * @param accountId int
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     *
     * @return Thymeleaf fragment name
     */
    @GetMapping("/invoices/cancel/{id}")
    public String cancelInvoice(@PathVariable("id") int accountId, Model model, RedirectAttributes redirectAttributes) {

        // reset model variables to default
        redirectAttributes.addFlashAttribute("invoiceMode", "default");
        redirectAttributes.addFlashAttribute("selectedInvoice", null);
        redirectAttributes.addFlashAttribute("accountId", accountId);

        // return to SPA view
        return "redirect:/ArmsSPA/" + accountId;
    }



    /**
     *  pay service stub
     *  placeholder for future 3rd party payment integration
     *
     * @param accountId int
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return Thymeleaf fragment name
     */
    @GetMapping("/invoices/pay/{id}")
    public String payInvoice(@PathVariable("id") int accountId, Model model, RedirectAttributes redirectAttributes) {

        // toggle to pay mode and reload accountId
        redirectAttributes.addFlashAttribute("invoiceMode", "pay");
        redirectAttributes.addFlashAttribute("accountId", accountId);

        // return to SPA view
        return "redirect:/ArmsSPA/" + accountId;
    }


    /**
     *  message service stub
     *  placeholder for future email integration
     *
     * @param accountId int
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     *
     * @return Thymeleaf fragment name
     */
    @GetMapping("/invoices/message/{id}")
    public String messageInvoice(@PathVariable("id") int accountId, Model model, RedirectAttributes redirectAttributes) {

        // toggle to message mode and reload accountId
        redirectAttributes.addFlashAttribute("invoiceMode", "message");
        redirectAttributes.addFlashAttribute("accountId", accountId);

        // return to SPA view
        return "redirect:/ArmsSPA/" + accountId;
    }


}
