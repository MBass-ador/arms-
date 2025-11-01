package com.basssoft.arms.account.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

/**
 * Account Controller
 * for handling account-related http requests
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    IaccountService accountService;


    /**
     * Create new Account
     *
     * @param accountDto AccountDTO
     * @return ResponseEntity with created AccountDto or error message
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDto) {
        try {
            // Call service to create account
            AccountDTO created = accountService.createAccount(accountDto);

            if (created == null) {
                // Creation failed
                return ResponseEntity.badRequest().body("Account could not be created.");
            }
            // Build location URI for created resource
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getAccountId())
                    .toUri();

            // return status: 201 and DTO of created account
            return ResponseEntity.created(location).body(created);

        } catch (IllegalArgumentException ex) {
            // Handle bad input
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }


    /**
     * Update existing Account
     *
     * @param id         int Account ID from path
     * @param accountDto AccountDTO with updated data
     * @return ResponseEntity with updated AccountDto or error message
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable int id, @RequestBody AccountDTO accountDto) {

        try {
            // id from path
            accountDto.setAccountId(id);

            // call service
            AccountDTO updated = accountService.updateAccount(accountDto);

            if (updated == null) {
                // no such entity
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
            }
            // success
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException ex) {
            // bad request
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }

}
