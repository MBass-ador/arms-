package com.basssoft.arms.booking.controller;

import com.basssoft.arms.account.service.IaccountService;
import com.basssoft.arms.booking.domain.BookingDTO;
import com.basssoft.arms.booking.service.IbookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/**
 *  tests BookingWebController class
 *
 * arms application
 * @author Matthew Bass
 * @version 4.0
 */
public class BookingWebControllerTest {

    private IbookingService bookingService;

    private BookingWebController controller;
    private IaccountService accountService;

    @BeforeEach
    void setUp() {
        bookingService = mock(IbookingService.class);
        accountService = mock(IaccountService.class);
        controller = new BookingWebController(bookingService, accountService);
    }


    /**
     * tests createBooking method
     */
    @Test
    void testCreateBooking() {

        BookingDTO form = new BookingDTO();
        BookingDTO created = new BookingDTO();
        created.setCustomer(42);

        when(bookingService.createBooking(any(BookingDTO.class))).thenReturn(created);
        BindingResult binding = mock(BindingResult.class);
        when(binding.hasErrors()).thenReturn(false);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        String view = controller.createBooking(form, binding, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/42", view);
        assertEquals(true, redirectAttributes.getFlashAttributes().get("createSuccess"));
        assertEquals("default", redirectAttributes.getFlashAttributes().get("bookMode"));
        assertEquals(42, redirectAttributes.getFlashAttributes().get("accountId"));
    }


    /**
     * tests getBooking method
     */
    @Test
    void testViewBooking() {
        BookingDTO b = new BookingDTO();
        b.setBookingId(2);
        // for redirect
        b.setCustomer(42);

        b.setProvider(7);
        // mock provider account
        var providerAccount = mock(com.basssoft.arms.account.domain.AccountDTO.class);
        when(providerAccount.getScreenName()).thenReturn("ProviderName");
        when(accountService.getAccount(7)).thenReturn(providerAccount);

        when(bookingService.getBooking(2)).thenReturn(b);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.viewBooking(2, model, redirectAttributes);

        BookingDTO selected = (BookingDTO) redirectAttributes.getFlashAttributes().get("selectedBooking");

        assertEquals("redirect:/ArmsSPA/42", view);
        assertNotNull(selected);
        assertEquals(2, selected.getBookingId());
        assertEquals(42, selected.getCustomer());
        assertEquals("ProviderName", selected.getProviderName());
        assertEquals(42, redirectAttributes.getFlashAttributes().get("accountId"));
        assertEquals("view", redirectAttributes.getFlashAttributes().get("bookMode"));
    }


    /**
     * tests getBookings method
     */
    @Test
    void testGetBookings() {
        BookingDTO b = new BookingDTO();
        b.setBookingId(1);
        when(bookingService.getCustomerBookings(1)).thenReturn(List.of(b));

        Model model = new ExtendedModelMap();
        String view = controller.getBookings(1, model);
        assertEquals("ArmsSPA", view);
        assertTrue(model.containsAttribute("bookings"));
    }


    /**
     * tests updateBooking method
     */
    @Test
    void testUpdateBooking() {

        //arrange
        BookingDTO form = new BookingDTO();
        BookingDTO existing = new BookingDTO();
        existing.setBookingId(5);
        existing.setCustomer(42);
        existing.setCompleted(true);
        existing.setOverHours(BigDecimal.ONE);
        existing.setPaid(false);

        BookingDTO updated = new BookingDTO();
        updated.setBookingId(5);
        updated.setCustomer(42);

        when(bookingService.getBooking(5)).thenReturn(existing);
        when(bookingService.updateBooking(any(BookingDTO.class))).thenReturn(updated);

        BindingResult binding = mock(BindingResult.class);
        when(binding.hasErrors()).thenReturn(false);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // act
        String view = controller.updateBooking(5, form, binding, model, redirectAttributes);

        // assert
        assertEquals("redirect:/ArmsSPA/42", view);
        assertEquals(true, redirectAttributes.getFlashAttributes().get("updateSuccess"));
        assertEquals("default", redirectAttributes.getFlashAttributes().get("bookMode"));
        assertEquals(42, redirectAttributes.getFlashAttributes().get("accountId"));
        // check bookingId
        ArgumentCaptor<BookingDTO> captor = ArgumentCaptor.forClass(BookingDTO.class);
        verify(bookingService).updateBooking(captor.capture());
        assertEquals(5, captor.getValue().getBookingId());
    }


    /**
     * tests deleteBooking method
     */
    @Test
    void testDeleteBooking() {

        // arrange
        int bookingId = 7;
        int accountId = 42;
        BookingDTO booking = new BookingDTO();
        booking.setBookingId(bookingId);
        booking.setCustomer(accountId);

        when(bookingService.getBooking(bookingId)).thenReturn(booking);
        when(bookingService.deleteBooking(bookingId)).thenReturn(bookingId);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        // act
        String view = controller.deleteBooking(bookingId, model, redirectAttributes);

        // assert
        assertEquals("redirect:/ArmsSPA/42", view);
        assertEquals("default", redirectAttributes.getFlashAttributes().get("bookMode"));
        assertEquals(true, redirectAttributes.getFlashAttributes().get("deleteSuccess"));
    }



    /**
     * tests cancelBooking method
     */
    @Test
    void testCancelButton() {

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.cancelButton(99, model, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/99", view);
        assertEquals("default", redirectAttributes.getFlashAttributes().get("bookMode"));
        assertNull(redirectAttributes.getFlashAttributes().get("selectedBooking"));
        assertEquals(99, redirectAttributes.getFlashAttributes().get("accountId"));
    }




    /**
     * tests createBookingMode method
     */
    @Test
    void testCreateMode() {

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.createMode(99, redirectAttributes);

        assertEquals("redirect:/ArmsSPA/99", view);
        assertEquals("create", redirectAttributes.getFlashAttributes().get("bookMode"));
        assertNull(redirectAttributes.getFlashAttributes().get("selectedBooking"));
        assertEquals(99, redirectAttributes.getFlashAttributes().get("accountId"));
    }




    /**
     * tests editMode method
     */
    @Test
    void testEditMode() {
        BookingDTO b = new BookingDTO();
        b.setBookingId(3);
        // for redirect
        b.setCustomer(42);

        b.setProvider(8);
        // mock provider account
        var providerAccount = mock(com.basssoft.arms.account.domain.AccountDTO.class);
        when(providerAccount.getScreenName()).thenReturn("ProviderName");
        when(accountService.getAccount(8)).thenReturn(providerAccount);

        when(bookingService.getBooking(3)).thenReturn(b);

        Model model = new ExtendedModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.editMode(3, model, redirectAttributes);

        BookingDTO selected = (BookingDTO) redirectAttributes.getFlashAttributes().get("selectedBooking");

        assertEquals("redirect:/ArmsSPA/42", view);
        assertNotNull(selected);
        assertEquals(3, selected.getBookingId());
        assertEquals(42, selected.getCustomer());
        assertEquals("ProviderName", selected.getProviderName());
        assertEquals(42, redirectAttributes.getFlashAttributes().get("accountId"));
        assertEquals("edit", redirectAttributes.getFlashAttributes().get("bookMode"));
    }



}
