package com.basssoft.arms.account.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

/**
 *  tests AccountWebController class
 *
 * arms application
 * @author Matthew Bass
 * @version 4.0
 */
public class AccountWebControllerTest {

    IaccountService accountService;
    AccountWebController controller;

    @BeforeEach
    void setUp() {
        accountService = mock(IaccountService.class);
        controller = new AccountWebController(accountService);
    }


    /**
     * tests editButton() method
     */
    @Test
    void testEditButton() {

        // mock model
        Model model = new ExtendedModelMap();

        // call method
        String view = controller.editButton(1, model);

        // verify correct view and model attribute
        assertEquals("ArmsSPA", view);
        assertEquals(true, model.getAttribute("editMode"));
    }



    /**
     * tests updateAccount() method
     */
    @Test
    void testUpdateAccount() {

        // set up mock data
        int accountId = 1;
        AccountDTO inputDto = new AccountDTO();
        inputDto.setAccountId(accountId);
        AccountDTO updatedDto = new AccountDTO();
        updatedDto.setAccountId(accountId);

        // set up mock behavior
        when(accountService.updateAccount(inputDto)).thenReturn(updatedDto);

        // mock model
        Model model = new ExtendedModelMap();

        // mock RedirectAttributes
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // call method
        String result = controller.updateAccount(accountId, inputDto, model, redirectAttributes);

        // verify correct redirect, editMode setting, and service call
        assertEquals("redirect:/ArmsSPA/1", result);
        assertEquals(false, model.getAttribute("editMode"));
        verify(accountService, times(1)).updateAccount(inputDto);
    }



    /**
     * tests cancelButton() method
     */
    @Test
    void testCancelButton() {

        // mock model
        Model model = new ExtendedModelMap();

        // call method
        String view = controller.cancelButton(1, model);

        // verify correct view and model attribute
        assertEquals("redirect:/ArmsSPA/1", view);
        assertEquals(false, model.getAttribute("editMode"));
    }


    /**
     * tests getAccountsHtml() method
     */
    @Test
    void testGetAccountsHtml() {
        AccountDTO dto = new AccountDTO();
        dto.setAccountId(1);
        List<AccountDTO> list = List.of(dto);
        when(accountService.getAllAccounts()).thenReturn(list);

        Model model = new ExtendedModelMap();
        String view = controller.getAccountsHtml(model);

        assertEquals("ArmsSPA", view);
        assertEquals(list, model.getAttribute("accounts"));
        verify(accountService, times(1)).getAllAccounts();
    }

}
