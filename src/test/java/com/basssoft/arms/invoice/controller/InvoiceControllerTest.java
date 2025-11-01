package com.basssoft.arms.invoice.controller;


import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for InvoiceController

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    static IinvoiceService invoiceService = Mockito.mock(IinvoiceService.class);

    @TestConfiguration
    static class TestConfig {
        @Bean
        IinvoiceService invoiceService() {
            return invoiceService;
        }
    }


    /**
     *  Tests {@link InvoiceController#createInvoice}
     */
    @Test
    void testCreateInvoice_Success() throws Exception {
        InvoiceDTO requestDto = new InvoiceDTO();
        requestDto.setProviderId(123);
        requestDto.setCustomerId(456);
        requestDto.setTotalAmountDue(250.75f);

        InvoiceDTO responseDto = new InvoiceDTO();
        responseDto.setInvoiceId(1);
        responseDto.setProviderId(123);
        responseDto.setCustomerId(456);
        responseDto.setTotalAmountDue(250.75f);

        when(invoiceService.createInvoice(any(InvoiceDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceId").value(1))
                .andExpect(jsonPath("$.providerId").value(123))
                .andExpect(jsonPath("$.customerId").value(456))
                .andExpect(jsonPath("$.totalAmountDue").value(250.75));
    }





    /**
     *  Tests {@link InvoiceController#updateInvoice}:success
     */
    @Test
    void testUpdateInvoice_Success() throws Exception {

        int invoiceId = 1;

        InvoiceDTO requestDto = new InvoiceDTO();
        requestDto.setProviderId(123);
        requestDto.setCustomerId(456);
        requestDto.setTotalAmountDue(300.00f);

        InvoiceDTO responseDto = new InvoiceDTO();
        responseDto.setInvoiceId(invoiceId);
        responseDto.setProviderId(123);
        responseDto.setCustomerId(456);
        responseDto.setTotalAmountDue(300.00f);

        when(invoiceService.updateInvoice(any(InvoiceDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/invoices/{id}", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(invoiceId))
                .andExpect(jsonPath("$.providerId").value(123))
                .andExpect(jsonPath("$.customerId").value(456))
                .andExpect(jsonPath("$.totalAmountDue").value(300.00));
    }


    /**
     *  Tests {@link InvoiceController#updateInvoice}:notFound
     */
    @Test
    void testUpdateInvoice_NotFound() throws Exception {

        int badId = 99;

        InvoiceDTO requestDto = new InvoiceDTO();
        requestDto.setProviderId(123);
        requestDto.setCustomerId(456);
        requestDto.setTotalAmountDue(300.00f);

        when(invoiceService.updateInvoice(any(InvoiceDTO.class))).thenReturn(null);

        mockMvc.perform(post("/invoices/{id}", badId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Invoice not found."));
    }













}
