package com.basssoft.arms.account.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Account Web Controller
 * for thymeleaf server-side rendered HTML views

 * arms application
 * @author Matthew Bass
 * @version 3.0
 */
@Controller
@RequestMapping("/accounts")
public class AccountWebController {

    // pair controller to view
    private static final String VIEW ="ArmsSPA";

    // dependencies
    private final IaccountService accountService;

    /**
     * Constructor for AccountWebController
     * injects dependencies

     * @param accountService IaccountService
     */
    public AccountWebController(IaccountService accountService) {
        this.accountService = accountService;
    }



    /**
     * Handle edit button click
     * enables edit mode in view

     * @param id    account id
     * @param model Model
     * @return HTML view name
     */
    @GetMapping(value = "/edit/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String editButton(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {

        //re-add account and id to model
        AccountDTO account = accountService.getAccount(id);
        redirectAttributes.addFlashAttribute("account", account);
        redirectAttributes.addFlashAttribute("accountId", id);

        // toggle edit mode on
        redirectAttributes.addFlashAttribute("editMode", true);

        // reload single page view
        return "redirect:/ArmsSPA/" + id;
    }



    /**
     * Handle account update form submission

     * @param id         account id
     * @param accountDto AccountDTO from form (formatted w @ModelAttribute)
     * @param model      Model from Spring MVC view
     * @return redirect to account view or error view
     */
    @PostMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updateAccount(@PathVariable int id,
                                @ModelAttribute("account") @Valid AccountDTO accountDto,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        // check for validation errors
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("account", accountDto);
            redirectAttributes.addFlashAttribute("editMode", true);
            redirectAttributes.addFlashAttribute("accountId", id);
            return "redirect:/" + VIEW + "/" + id;
        }

        // call service to update account
        AccountDTO updatedAccount = accountService.updateAccount(accountDto);

        // check for null (indicates failure)
        if (updatedAccount == null  || !updatedAccount.getAccountId().equals(id)) {
            model.addAttribute("error", "Account update failed.");
            return "error";
        }

        // toggle edit mode off
        model.addAttribute("editMode", false);
        model.addAttribute("account", updatedAccount);
        model.addAttribute("AccountId", id);


        // add success message
        redirectAttributes.addFlashAttribute("updateSuccess", true);

        // redirect to updated account view
        return "redirect:/" + VIEW + "/" + id;
    }



    /**
     * Handle cancel button click
     * disables edit mode in view

     * @param id    account id
     * @param model Model
     * @return HTML view name
     */
    @GetMapping(value = "/cancel/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String cancelButton(@PathVariable int id, Model model) {

        // toggle edit mode off
        model.addAttribute("editMode", false);

        //re-add account and id to model
        AccountDTO account = accountService.getAccount(id);
        model.addAttribute("account", account);
        model.addAttribute("AccountId", id);

        System.out.println("cancelButton called for account id: " + id);

        // reload single page view
        return "redirect:/" + VIEW + "/" + id;
    }



    /**
     * Get all accounts and render as HTML

     * @param model Model
     * @return HTML view name
     */
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getAccountsHtml(Model model) {

        // get all accounts via service
        List<AccountDTO> accounts = accountService.getAllAccounts();


        // add to model
        model.addAttribute("accounts", accounts);

        // return view name
        return VIEW;
    }


}
