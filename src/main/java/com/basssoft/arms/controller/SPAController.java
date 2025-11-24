package com.basssoft.arms.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.service.IbookingService;
import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Single Page Application (SPA) Controller
 * for serving SPA views with dynamic parameters.
 *
 * arms application
 * @author Matthew Bass
 * @version 4.0
 */
@Controller
public class SPAController {

    // dependencies
    private final IaccountService accountService;
    private final IbookingService bookingService;
    private final IinvoiceService invoiceService;


    /**
     * constructor for SPAController
     * injects dependencies
     *
     * @param accountService IaccountService
     * @param bookingService IbookingService
     * @param invoiceService IinvoiceService
     */
    public SPAController
                (IaccountService accountService, IbookingService bookingService, IinvoiceService invoiceService) {
        this.accountService = accountService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
    }


    /**
     * Load providers for form select options
     *
     * @return Map of provider IDs and names
     */
    @ModelAttribute("providers")
    public Map<Integer, String> populateProviders() {

        return accountService.getAllAccounts().stream()
                .collect(Collectors.toMap(AccountDTO::getAccountId, AccountDTO::getScreenName));
    }


    /**
     * Serve ArmsSPA view with accountId.
     *
     * @param accountId the account ID
     * @param model     the model to add attributes to
     * @param providers map of provider IDs to names
     *
     * @return String the name of the SPA view
     */
    @GetMapping(value = "/ArmsSPA/{accountId}", produces = MediaType.TEXT_HTML_VALUE)
    public String armsSpa(@PathVariable int accountId, Model model, @ModelAttribute("providers") Map<Integer, String> providers) {

        try {
            // get account via service
            AccountDTO account = accountService.getAccount(accountId);

            if (account != null) {

                // add account to model
                model.addAttribute("account", account);
                model.addAttribute("accountId", accountId);

                // load bookings for this account
                List<BookingDTO> bookings = bookingService.getCustomerBookings(accountId);

                // set provider and customer names for each booking
                for (BookingDTO booking : bookings) {
                    booking.setProviderName(providers.get(booking.getProvider()));
                    booking.setCustomerName(account.getScreenName());
                }

                // log bookings to troubleshoot
                System.out.println("Bookings for account " + accountId + ":");
                for (BookingDTO booking : bookings) {
                    System.out.println("BookingId: " + booking.getBookingId()
                                    + ", Provider: " + booking.getProvider()
                                    + ", Customer: " + booking.getCustomer()
                                    + ", ProviderName: " + booking.getProviderName()
                                    + ", CustomerName: " + booking.getCustomerName()
                                    + ", StartTime: " + booking.getStartTime()
                                    + ", EndTime: " + booking.getEndTime()
                    );;
                }
                model.addAttribute("bookings", bookings);

                // load invoices for this account
                List<InvoiceDTO> invoices = invoiceService.getCustomerInvoices(accountId);

                // log invoices to troubleshoot
                System.out.println("Invoices for account " + accountId + ":");
                for (InvoiceDTO invoice : invoices) {
                    System.out.println("InvoiceId: " + invoice.getInvoiceId()
                            + ", ProviderId: " + invoice.getProviderId()
                            + ", CustomerId: " + invoice.getCustomerId()
                            + ", BookingIds: " + invoice.getBookingIds()
                            + ", TotalAmountDue: " + invoice.getTotalAmountDue()
                            + ", LastContacted: " + invoice.getLastContacted()
                    );
                }
                // add list of invoices to model
                model.addAttribute("invoices", invoices);

                // editMode = False (unless set otherwise)
                if (!model.containsAttribute("editMode")) {
                    model.addAttribute("editMode", false);
                }
                // bookMode = default (unless set otherwise)
                if (!model.containsAttribute("bookMode")) {
                    model.addAttribute("bookMode", "default");
                }
                // invoiceMode = default (unless set otherwise)
                if (!model.containsAttribute("invoiceMode")) {
                    model.addAttribute("invoiceMode", "default");
                }
            } else {
                // still provide the accountId so guarded fragments work; account remains null
                model.addAttribute("accountId", accountId);
            }
            // to SPA view
            return "ArmsSPA";

        } catch (Exception ex) {
            // handle exceptions with model error attribute
            model.addAttribute("error", "Unable to load account.");
            return "error";
        }
    }

}
