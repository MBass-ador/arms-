package com.basssoft.arms.account.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.hateoas.EntityModel;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private AccountModelAssembler assembler;


    /**
     * Create new Account
     *
     * @param accountDto AccountDTO
     * @return ResponseEntity with created AccountDto and status
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountDTO accountDto) {
        try {
            // call service / create account
            AccountDTO created = accountService.createAccount(accountDto);

            if (created == null) {
                // creation failed status:400
                return ResponseEntity.badRequest().body("Account could not be created.");
            }
            // build location URI for created resource
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getAccountId())
                    .toUri();

            // wrap created account w HATEOAS links
            EntityModel<AccountDTO> resource = assembler.toModel(created);

            // return created account status: 201
            return ResponseEntity.created(location).body(resource);

        } catch (IllegalArgumentException ex) {
            // handle bad request status:400
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // handle other exceptions status:500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }


    /**
     * Get Account by ID
     *
     * @param id int Account ID from path
     * @return ResponseEntity with AccountDTO and/or status
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable int id) {

        try {
            // call service / get account
            AccountDTO account = accountService.getAccount(id);

            // no account found status:404
            if (account == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
            }

            // wrap account w HATEOAS links
            EntityModel<AccountDTO> resource = assembler.toModel(account);

            // return account, success status:200
            return ResponseEntity.ok(resource);

        } catch (IllegalArgumentException iae) {
            // handle bad request status:400
            return ResponseEntity.badRequest().body(iae.getMessage());

        }catch (Exception ex) {
            // handle other errors status:500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error." + ex.getMessage());
        }
    }


    /**
     * Get all Accounts
     *
     * @return ResponseEntity with List of AccountDTOs and or status code
     */
    @GetMapping
    public ResponseEntity<?> getAccounts() {

        try {
            // call service / get accounts
            List<AccountDTO> accounts = accountService.getAllAccounts();

            // wrap accounts w HATEOAS links
            List<EntityModel<AccountDTO>> accountResources = accounts.stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            // create collection of link wrapped DTOs
            CollectionModel<EntityModel<AccountDTO>> collection = CollectionModel.of(accountResources);

            // return collection, success status:200
            return ResponseEntity.ok(collection);

        } catch (IllegalArgumentException iae) {
            // handle bad request status:400
            return ResponseEntity.badRequest().body(iae.getMessage());

        }catch (Exception ex) {
            // handle other errors status:500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error.");
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
                // no such entity status:404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
            }
            // wrap updated account w HATEOAS links
            EntityModel<AccountDTO> resource = assembler.toModel(updated);

            // return updated account, wrapped in links / success status:200
            return ResponseEntity.ok(resource);

        } catch (IllegalArgumentException ex) {
            // bad request status:400
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            // other errors status:500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }



    /**
     * Delete Account by ID
     *
     * @param id int Account ID from path
     * @return ResponseEntity with success message and or status code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable int id) {
        try {
            // call service / delete account
            int deleted =  accountService.deleteAccount(id);

            if (deleted != id) {
                // no such entity status:404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
            }
            // success status:200
            return ResponseEntity.ok("Account deleted successfully.");

        } catch (IllegalArgumentException iae) {
            // bad request status:400
            return ResponseEntity.badRequest().body(iae.getMessage());

        } catch (Exception ex) {
            // other errors status:500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error.");
        }
    }


}
