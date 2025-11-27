package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import com.basssoft.arms.invoice.service.InvoiceFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Test class for {@link InvoiceFactoryController}
 *
 * arms application
 * @author Matthew Bass
 * @version 5.0
 */
@WebFluxTest(InvoiceFactoryController.class)
@SuppressWarnings("removal")
public class InvoiceFactoryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountRepository accountRepo;

    @MockBean
    private InvoiceFactory invoiceFactory;



    /**
     * Test generateInvoice method
     */
    @Test
    public void testGenerateInvoice() {

        Invoice mockInvoice = new Invoice();
        Mockito.when(invoiceFactory.generateInvoice(anyInt(), anyInt()))
                .thenReturn(Mono.just(mockInvoice));

        webTestClient.get()
                .uri("/api/invoice-factory/generate?providerId=1&customerId=2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Invoice.class);
    }




    /**
     * Test generateAllProviderInvoices method
     */
    @Test
    public void testGenerateAllProviderInvoices() {

        // arrange
        int providerId = 1;
        List<Integer> customerIds = List.of(2, 3);
        Invoice mockInvoice1 = new Invoice();
        Invoice mockInvoice2 = new Invoice();

        Mockito.when(invoiceFactory.unpaidCustomerAccounts(providerId))
                .thenReturn(reactor.core.publisher.Flux.fromIterable(customerIds));
        Mockito.when(invoiceFactory.generateInvoice(providerId, 2))
                .thenReturn(Mono.just(mockInvoice1));
        Mockito.when(invoiceFactory.generateInvoice(providerId, 3))
                .thenReturn(Mono.just(mockInvoice2));

        // act & assert
        webTestClient.get()
                .uri("/api/invoice-factory/generate-all?providerId=" + providerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Invoice.class)
                .hasSize(2);
    }




}
