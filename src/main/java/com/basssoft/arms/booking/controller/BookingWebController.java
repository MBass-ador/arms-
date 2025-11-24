package com.basssoft.arms.booking.controller;


import com.basssoft.arms.account.service.IaccountService;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.service.IbookingService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.List;

/**
 *  BookingWebController class
 *  for thymeleaf server-side rendered HTML views
 *
 * arms application
 * @author Matthew Bass
 * @version 4.0
 */
@Controller
public class BookingWebController {

    // pair controller to view
    private static final String VIEW ="ArmsSPA";

    // dependencies
    private final IbookingService bookingService;
    private final IaccountService accountService;


    /**
     * Constructor for BookingWebController
     * injects dependencies
     *
     * @param bookingService IbookingService
     * @param accountService IaccountService
     */
    public BookingWebController(IbookingService bookingService, IaccountService accountService) {
        this.bookingService = bookingService;
        this.accountService = accountService;
    }


    /**
     * Create new Booking from HTML form submission
     *
     * @param form          BookingDTO form data
     * @param bindingResult BindingResult for validation
     * @param model         Model
     * @param redirectAttributes RedirectAttributes
     * @return HTML view name or redirect
     */
    @PostMapping(value = "/bookings", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "text/html")
    public String createBooking(@ModelAttribute("booking") BookingDTO form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            // validate form input
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("booking", form);
                redirectAttributes.addFlashAttribute("bookMode", "create");
                redirectAttributes.addFlashAttribute("accountId", form.getCustomer());

                // back to form
                return "redirect:/" + VIEW + "/" + form.getCustomer();
            }
            // set default values
            form.setCompleted(false);
            form.setOverHours(BigDecimal.ZERO);
            form.setPaid(false);

            // create booking via service
            BookingDTO created = bookingService.createBooking(form);

            // add success attributes
            redirectAttributes.addFlashAttribute("createSuccess", true);
            redirectAttributes.addFlashAttribute("bookMode", "default");
            redirectAttributes.addFlashAttribute("accountId", created.getCustomer());


            // redirect to list of bookings
            return "redirect:/" + VIEW + "/" + created.getCustomer();

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to create booking.");
            return "error";
        }
    }


    /**
     * Get Booking by ID and render as HTML
     *
     * @param id    booking ID from path
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return HTML view name
     */
    @GetMapping(value = "/bookings/view/{id}", produces = "text/html")
    public String viewBooking(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {

        try {
            // get booking via service
            BookingDTO booking = bookingService.getBooking(id);

            // when found
            if (booking != null) {
                // lookup provider name and set it
                String providerName = accountService.getAccount(booking.getProvider()).getScreenName();
                booking.setProviderName(providerName);

                // add to model
                redirectAttributes.addFlashAttribute("selectedBooking", booking);
                redirectAttributes.addFlashAttribute("accountId", booking.getCustomer());
                redirectAttributes.addFlashAttribute("bookMode", "view");

                // return to controller
                return "redirect:/ArmsSPA/" + booking.getCustomer();

            } else {
                // handle not found
                model.addAttribute("error", "Booking not found.");
                return "error";
            }

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to load booking.");
            return "error";
        }
    }


    /**
     * Get all Bookings and render as HTML
     *
     * @param customerId customer ID
     * @param model Model
     * @return HTML view name
     */
    @GetMapping(value = "/bookings", produces = "text/html")
    public String getBookings(int customerId, Model model) {

        try {
            // get all bookings via service
            List<BookingDTO> bookings = bookingService.getCustomerBookings(customerId);

            // add to model
            model.addAttribute("bookings", bookings);

            // return to default booking list view
            return VIEW;

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to load bookings.");
            return "error";
        }
    }


    /**
     * Update existing Booking from HTML form submission
     *
     * @param id            booking ID from path
     * @param form          BookingDTO form data
     * @param bindingResult BindingResult for validation
     * @param model         Model
     * @param redirectAttributes RedirectAttributes
     * @return HTML view name or redirect
     */
    @PostMapping(value = "/bookings/update/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "text/html")
    public String updateBooking(@PathVariable int id,
                                @ModelAttribute("booking") BookingDTO form,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            // validate form input
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("booking", form);
                redirectAttributes.addFlashAttribute("bookMode", "edit");
                redirectAttributes.addFlashAttribute("accountId", form.getCustomer());

                // back to form
                return "redirect:/" + VIEW + "/" + form.getCustomer();
            }

            // set ID from path variable
            form.setBookingId(id);

            // preserve existing values for fields not in the form
            BookingDTO existing = bookingService.getBooking(id);
            if (existing != null) {
                form.setCompleted(existing.isCompleted());
                form.setOverHours(existing.getOverHours());
                form.setPaid(existing.isPaid());
            }

            // update booking via service
            BookingDTO updated = bookingService.updateBooking(form);

            // handle not found
            if (updated == null) {
                model.addAttribute("error", "Booking not found.");
                return "error";
            }

            // add success attributes
            redirectAttributes.addFlashAttribute("updateSuccess", true);
            redirectAttributes.addFlashAttribute("bookMode", "default");
            redirectAttributes.addFlashAttribute("accountId", updated.getCustomer());

            // return to default booking list view
            return "redirect:/" + VIEW + "/" + updated.getCustomer();

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to update booking.");
            return "error";
        }
    }


    /**
     *  delete Booking by ID from HTML form submission
     *
     * @param id    booking ID from path
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return HTML view name or redirect
     */
    @PostMapping( value= "/bookings/delete/{id}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,produces = "text/html")
    public String deleteBooking(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {

        try {
            // get existing booking to check completed status
            BookingDTO existing = bookingService.getBooking(id);
            if (existing == null) {
                model.addAttribute("error", "Booking not found.");
                return "error";
            }
            // prevent deletion if completed
            if (existing.isCompleted()) {
                redirectAttributes.addFlashAttribute("cantDelete", true);
                redirectAttributes.addFlashAttribute("bookMode", "cantDelete");
                redirectAttributes.addFlashAttribute("accountId", existing.getCustomer());
                return "redirect:/" + VIEW + "/" + existing.getCustomer();
            }

            int accountId = existing.getCustomer();

            // delete booking via service
            int deletedId = bookingService.deleteBooking(id);

            // redirect to bookings list if successful
            if (deletedId == id) {
                // set success attributes
                redirectAttributes.addFlashAttribute("bookMode", "default");
                redirectAttributes.addFlashAttribute("deleteSuccess", true);
                redirectAttributes.addFlashAttribute("selectedBooking", null);

                return "redirect:/" + VIEW + "/" + accountId;

            } else {
                // handle not found
                model.addAttribute("error", "Booking not found.");
                return "error";
            }
        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to delete booking.");
            return "error";
        }
    }



    /**
     *  cancel current bookingMode
     *  and reload default SPA view through controller
     *
     * @param accountId        account ID
     * @param model         Model
     * @param redirectAttributes RedirectAttributes
     * @return redirect to ArmsSPA controller
     */
    @GetMapping("/bookings/cancel/{id}")
    public String cancelButton(@PathVariable("id") int accountId, Model model, RedirectAttributes redirectAttributes) {

        // reset model attributes to default
        redirectAttributes.addFlashAttribute("bookMode", "default");
        redirectAttributes.addFlashAttribute("selectedBooking", null);
        redirectAttributes.addFlashAttribute("accountId", accountId);

        // return to view via controller
        return "redirect:/ArmsSPA/" + accountId;
    }



    /**
     *  set bookingMode to create
     *  reload SPA view through controller
     *
     * @param accountId        account ID
     * @param redirectAttributes RedirectAttributes
     * @return redirect to ArmsSPA controller
     */
    @GetMapping("/bookings/createMode/{accountId}")
    public String createMode(@PathVariable int accountId, RedirectAttributes redirectAttributes) {

        // update model
        redirectAttributes.addFlashAttribute("bookMode", "create");
        redirectAttributes.addFlashAttribute("selectedBooking", null);
        redirectAttributes.addFlashAttribute("accountId", accountId);

        // return to view via controller
        return "redirect:/ArmsSPA/" + accountId;
    }



    /**
     * Set bookingMode to edit and load selected booking
     *
     * @param id the booking ID
     * @param model Model
     * @param redirectAttributes RedirectAttributes
     * @return redirect to ArmsSPA controller
     */
    @GetMapping("/bookings/editMode/{id}")
    public String editMode(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {

        try {
            // get booking via service
            BookingDTO booking = bookingService.getBooking(id);

            // when found
            if (booking != null) {
                // lookup provider name and set it
                String providerName = accountService.getAccount(booking.getProvider()).getScreenName();
                booking.setProviderName(providerName);

                // add to model
                redirectAttributes.addFlashAttribute("selectedBooking", booking);
                redirectAttributes.addFlashAttribute("accountId", booking.getCustomer());
                redirectAttributes.addFlashAttribute("bookMode", "edit");

                // return to controller
                return "redirect:/ArmsSPA/" + booking.getCustomer();

            } else {
                // handle not found
                model.addAttribute("error", "Booking not found.");
                return "error";
            }

        } catch (Exception ex) {
            // handle errors
            model.addAttribute("error", "Unable to load booking.");
            return "error";
        }
    }

}
