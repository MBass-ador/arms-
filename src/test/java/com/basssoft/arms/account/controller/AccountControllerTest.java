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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for AccountController

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    // to simulate HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // mock the service layer
    static IaccountService accountService = Mockito.mock(IaccountService.class);

    // get mocked service
    @TestConfiguration
    static class TestConfig {
        @Bean
        IaccountService accountService() {
            return accountService;
        }
    }


    /**
     * Tests @Link com.basssoft.account.controller.AccountController#createAccount
     */
    @Test
    void testCreateAccount() throws Exception {
        AccountDTO mockAccountDto = new AccountDTO(
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
                .andExpect(jsonPath("$.screenName").value("testuser"));
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




}
