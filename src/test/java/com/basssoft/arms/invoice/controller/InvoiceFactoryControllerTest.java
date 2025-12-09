package com.basssoft.arms.invoice.controller;

import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.invoice.domain.Invoice;
import com.basssoft.arms.invoice.service.InvoiceFactory;
import com.basssoft.arms.security.config.SecurityConfig;
import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

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
@AutoConfigureWebTestClient

public class InvoiceFactoryControllerTest {

    //default security config for tests
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.authorizeExchange().anyExchange().permitAll().and().csrf().disable().build();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private ArmsUserDetailsService userDetailsService;

    @MockBean
    private AccountRepository accountRepo;

    @MockBean
    private InvoiceFactory invoiceFactory;

    private String jwtToken;


    /**  setup method initializes mock JWT token
     */
    @BeforeEach
    public void setup() {

        // dummy token and username
        jwtToken = "mocked.jwt.token";
        String username = "providerUser";

        // mock UserDetails with PROVIDER role
        UserDetails userDetails = User.withUsername(username)
                .password("password")
                .roles("PROVIDER")
                .build();

        // mock JWTService and UserDetailsService behavior
        Mockito.when(jwtService.extractUsername(jwtToken)).thenReturn(username);
        Mockito.when(jwtService.isTokenValid(jwtToken, userDetails)).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
    }


    /**
     * Helper method
     * adds Authorization header to requests
     *
     * @param spec WebTestClient.RequestHeadersUriSpec<?>
     * @return WebTestClient.RequestHeadersSpec<?>
     */
    private WebTestClient.RequestHeadersSpec<?> _addAuthHeader(WebTestClient.RequestHeadersUriSpec<?> spec) {

        return spec.header("Authorization", "Bearer " + jwtToken);
    }




    /**
     * Test generateInvoice method
     */
    @Test
    public void testGenerateInvoice() {

        Invoice mockInvoice = new Invoice();
        Mockito.when(invoiceFactory.generateInvoice(anyInt(), anyInt()))
                .thenReturn(Mono.just(mockInvoice));

        WebTestClient.RequestHeadersUriSpec<?> requestSpec =
                (WebTestClient.RequestHeadersUriSpec<?>) webTestClient.get()
                    .uri("/api/invoice-factory/generate?providerId=3&customerId=2")
                    .accept(MediaType.APPLICATION_JSON);

        _addAuthHeader(requestSpec)
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
