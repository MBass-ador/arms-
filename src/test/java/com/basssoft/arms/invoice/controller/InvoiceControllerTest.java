package com.basssoft.arms.invoice.controller;


import com.basssoft.arms.invoice.domain.InvoiceDTO;
import com.basssoft.arms.invoice.service.IinvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Unit tests for InvoiceController

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@WebMvcTest(InvoiceController.class)
@Import(InvoiceControllerTest.TestConfig.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IinvoiceService invoiceService;

    @Autowired
    private InvoiceModelAssembler assembler;

    @TestConfiguration
    static class TestConfig {
        @Bean
        IinvoiceService invoiceService() {
            return Mockito.mock(IinvoiceService.class);
        }

        @Bean
        InvoiceModelAssembler invoiceModelAssembler() {
            return Mockito.mock(InvoiceModelAssembler.class);
        }
    }

    private InvoiceDTO responseDto;

    /**
     * setup before tests
     */
    @BeforeEach
    void setUp() {
        responseDto = new InvoiceDTO();
        responseDto.setInvoiceId(1);
        responseDto.setProviderId(123);
        responseDto.setCustomerId(456);
        responseDto.setTotalAmountDue(250.75f);

        EntityModel<InvoiceDTO> model = EntityModel.of(responseDto,
                linkTo(methodOn(InvoiceController.class).getInvoice(1)).withSelfRel()
        );

        when(assembler.toModel(any(InvoiceDTO.class)))
                .thenAnswer(invocation -> {

            InvoiceDTO dto = invocation.getArgument(0);

            return EntityModel.of(dto,
                    linkTo(methodOn(InvoiceController.class)
                            .getInvoice(dto.getInvoiceId()))
                            .withSelfRel()
            );
        });
    }

    /**
     *  Tests {@link InvoiceController#createInvoice}
     */
    @Test
    void testCreateInvoice() throws Exception {
        InvoiceDTO requestDto = new InvoiceDTO();
        requestDto.setProviderId(123);
        requestDto.setCustomerId(456);
        requestDto.setTotalAmountDue(250.75f);

        when(invoiceService.createInvoice(any(InvoiceDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.invoiceId").value(1))
                .andExpect(jsonPath("$.providerId").value(123))
                .andExpect(jsonPath("$.customerId").value(456))
                .andExpect(jsonPath("$.totalAmountDue").value(250.75))
                .andExpect(jsonPath("$._links.self.href").exists());
    }


    /**
     *  Tests {@link InvoiceController#getInvoice}:success
     */
    @Test
    void testGetInvoice_Success() throws Exception {
        int invoiceId = 1;
        InvoiceDTO responseDto = new InvoiceDTO();
        responseDto.setInvoiceId(invoiceId);
        responseDto.setProviderId(123);
        responseDto.setCustomerId(456);
        responseDto.setTotalAmountDue(250.75f);

        when(invoiceService.getInvoice(invoiceId)).thenReturn(responseDto);

        mockMvc.perform(get("/invoices/{id}", invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(invoiceId))
                .andExpect(jsonPath("$.providerId").value(123))
                .andExpect(jsonPath("$.customerId").value(456))
                .andExpect(jsonPath("$.totalAmountDue").value(250.75))
                .andExpect(jsonPath("$._links.self.href").exists());
    }


    /**
     *  Tests {@link InvoiceController#getInvoice}:notFound
     */
    @Test
    void testGetInvoice_NotFound() throws Exception {
        int badId = 99;
        when(invoiceService.getInvoice(badId)).thenReturn(null);

        mockMvc.perform(get("/invoices/{id}", badId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Invoice not found."));
    }


    /**
     *  Tests {@link InvoiceController#getInvoices}
     */
    @Test
    void testGetInvoices() throws Exception {

        InvoiceDTO invoiceDto = new InvoiceDTO();
        invoiceDto.setInvoiceId(1);
        invoiceDto.setTotalAmountDue(100.0f);

        List<InvoiceDTO> invoices = List.of(invoiceDto);
        Mockito.when(invoiceService.getAllInvoices()).thenReturn(invoices);

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.invoiceDTOList[0].invoiceId").value(1))
                .andExpect(jsonPath("$._embedded.invoiceDTOList[0].totalAmountDue").value(100.0))
                .andExpect(jsonPath("$._embedded.invoiceDTOList[0]._links.self.href").exists());
        ;
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

        mockMvc.perform(put("/invoices/{id}", invoiceId)
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

        mockMvc.perform(put("/invoices/{id}", badId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Invoice not found."));
    }



    /**
     *  Tests {@link InvoiceController#deleteInvoice}:success
     */
    @Test
    void testDeleteInvoice_Success() throws Exception {

        Mockito.when(invoiceService.deleteInvoice(1)).thenReturn(1);

        mockMvc.perform(delete("/invoices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Invoice deleted successfully."));
    }



    /**
     *  Tests {@link InvoiceController#deleteInvoice}:notFound
     */
    @Test
    void testDeleteInvoice_NotFound() throws Exception {
        Mockito.when(invoiceService.deleteInvoice(99)).thenReturn(-1);

        mockMvc.perform(delete("/invoices/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Invoice not found."));
    }



}
