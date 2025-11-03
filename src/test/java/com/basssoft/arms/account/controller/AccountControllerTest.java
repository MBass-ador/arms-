package com.basssoft.arms.account.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for AccountController

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@WebMvcTest(AccountController.class)
@Import(AccountControllerTest.TestConfig.class)
public class AccountControllerTest {

    // to simulate HTTP requests
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IaccountService accountService;

    @Autowired
    private AccountModelAssembler accountModelAssembler;

    private AccountDTO mockAccountDto;

    @TestConfiguration
    static class TestConfig {

        @Bean
        IaccountService accountService() {
            return Mockito.mock(IaccountService.class);
        }
        @Bean
        AccountModelAssembler accountModelAssembler() {
            return Mockito.mock(AccountModelAssembler.class);
        }
    }


    /**
     * * setup for tests
     */
    @BeforeEach
    void setUp() {

        mockAccountDto = new AccountDTO(
                1,
                "testuser",
                "password123",
                false,
                "Test",
                "User",
                "test@example.com",
                "1234567890",
                "123 Main St",
                "Testville",
                "TS",
                "12345"
        );

        when(accountModelAssembler.toModel(any(AccountDTO.class)))
                .thenAnswer(invocation ->
                        EntityModel.of(
                                invocation.getArgument(0),
                                linkTo(methodOn(AccountController.class)
                                        .getAccount(1))
                                        .withSelfRel()
                        )
                );
    }



    /**
     * Tests @Link com.basssoft.account.controller.AccountController#createAccount
     */
    @Test
    void testCreateAccount() throws Exception {

        when(accountService.createAccount(any(AccountDTO.class))).thenReturn(mockAccountDto);

        String accountJson = """
        {
            "screenName": "testuser",
            "password": "password123",
            "isProvider": false,
            "firstName": "Test",
            "lastName": "User",
            "email": "test@example.com",
            "phoneNumber": "1234567890",
            "street": "123 Main St",
            "city": "Testville",
            "state": "TS",
            "zipCode": "12345"
        }
        """;

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.screenName").value("testuser"))
                .andExpect(jsonPath("$._links.self.href").exists());;
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#getAccount:success
     */
    @Test
    void testGetAccount_Success() throws Exception {

        when(accountService.getAccount(1)).thenReturn(mockAccountDto);

        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.screenName").value("testuser"));
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#getAccount:notFound
     */
    @Test
    void testGetAccount_NotFound() throws Exception {

        when(accountService.getAccount(99)).thenReturn(null);

        mockMvc.perform(get("/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Account not found."));
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#getAccounts
     */
    @Test
    void testGetAccounts() throws Exception {
        List<AccountDTO> accounts = List.of(mockAccountDto);
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.accountDTOList[0].accountId")
                        .value(1))
                .andExpect(jsonPath("$._embedded.accountDTOList[0].screenName")
                        .value("testuser"));
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#updateAccount:success
     */
    @Test
    void testUpdateAccount_Success() throws Exception {

        AccountDTO updatedDto = new AccountDTO(1, "updateduser",
                "password123", false, "Test", "User",
                "test@example.com", "1234567890", "123 Main St",
                "Testville", "TS", "12345");

        when(accountService.updateAccount(any(AccountDTO.class))).thenReturn(updatedDto);

        String updateJson = """
    {
        "screenName": "updateduser",
        "password": "password123",
        "isProvider": false,
        "firstName": "Test",
        "lastName": "User",
        "email": "test@example.com",
        "phoneNumber": "1234567890",
        "street": "123 Main St",
        "city": "Testville",
        "state": "TS",
        "zipCode": "12345"
    }
    """;

        mockMvc.perform(put("/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.screenName").value("updateduser"));
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#updateAccount:notFound
     */
    @Test
    void testUpdateAccount_NotFound() throws Exception {

        when(accountService.updateAccount(any(AccountDTO.class))).thenReturn(null);

        String updateJson = "{ \"screenName\": \"nouser\" }";

        mockMvc.perform(put("/accounts/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#deleteAccount:success
     */
    @Test
    void testDeleteAccount_Success() throws Exception {

        when(accountService.deleteAccount(1)).thenReturn(1);

        mockMvc.perform(delete("/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Account deleted successfully."));
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#deleteAccount:notFound
     */
    @Test
    void testDeleteAccount_NotFound() throws Exception {

        when(accountService.deleteAccount(99)).thenReturn(-1);

        mockMvc.perform(delete("/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Account not found."));
    }


}
