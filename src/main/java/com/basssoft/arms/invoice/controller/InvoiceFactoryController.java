package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import com.basssoft.arms.invoice.service.InvoiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

/**
 * Controller for Invoice Factory APIs
 *
 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@RestController
@RequestMapping("/api/invoice-factory")
public class InvoiceFactoryController {

    @Autowired
    private InvoiceFactory invoiceFactory;

    @Autowired
    private AccountRepository accountRepo;



    /**
     * Generate Invoice for Customer
     *
     * @param customerId int
     * @return Mono<Invoice>
     */
    @GetMapping("/generate")
    public Mono<ResponseEntity<Invoice>> generateInvoice(@RequestParam int providerId, @RequestParam int customerId) {

        return invoiceFactory.generateInvoice(providerId, customerId)
                .map(invoice -> ResponseEntity.ok(invoice))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }


    /**
     *  Generate all outstanding
     *  Invoices for Provider (for all customers)
     *  calls getAllUnpaidCustomerAccounts() in InvoiceFactory to get list of customer IDs associated with unpaid bookings
     *  then generates Invoice for each customer by calling generateInvoice() in InvoiceFactory
     *
     * @param providerId int
     * @return Flux<Invoice>
     */
    @GetMapping("/generate-all")
    public Flux<Invoice> generateAllProviderInvoices(@RequestParam int providerId) {

        return invoiceFactory.unpaidCustomerAccounts(providerId)
                // limited concurrency; retry on failure then skip if still failing
                .flatMap(customerId ->
                                invoiceFactory.generateInvoice(providerId, customerId)
                                        .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
                                        .onErrorResume(ex -> Mono.empty())
                        , 5)
                // buffer under backpressure
                .onBackpressureBuffer(100);
    }

}
