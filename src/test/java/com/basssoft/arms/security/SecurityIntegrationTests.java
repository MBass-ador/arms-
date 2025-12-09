package com.basssoft.arms.security;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.repository.AccountRepository;
import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Arrays;
import java.util.Map;
import jakarta.servlet.http.Cookie;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.context.WebApplicationContext;

/** * SecurityIntegrationTests
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ArmsUserDetailsService userDetailsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext context;


    private Account testAccount;



    @AfterEach
    void cleanupTestUsers() {
        accountRepository.findByScreenName("happyUser")
                .ifPresent(accountRepository::delete);
        accountRepository.findByScreenName("refreshUser")
                .ifPresent(accountRepository::delete);
        accountRepository.findByScreenName("testCustomer")
                .ifPresent(accountRepository::delete);
    }



    /** tests that protected endpoint requires authentication
     *
     * @throws Exception Exception
     */
    @Test
    void protectedEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isUnauthorized());
    }


    /** tests that access is forbidden for user with insufficient role
     *
     * @throws Exception Exception
     */
    @Test
    void forbiddenForInsufficientRole() throws Exception {

        // create test user (only CUSTOMER role)
        Account testAccount = new Account();
        testAccount.setScreenName("testCustomer");
        testAccount.setPassword(passwordEncoder.encode("password"));
        testAccount.setFirstName("testFirstName");
        testAccount.setLastName("testLastName");
        testAccount.setPhoneNumber("1234567");
        testAccount.setEmail("test@email.com");
        testAccount.setStreet("testStreet");
        testAccount.setCity("testCity");
        testAccount.setState("TS");
        testAccount.setZipCode("12345");
        testAccount.setProvider(false);
        accountRepository.save(testAccount);

        // generate JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername("testCustomer");
        String jwt = jwtService.generateToken(userDetails, Map.of("accountId", testAccount.getAccountId()));

        // make request with JWT
        mockMvc.perform(get("/api/invoice-factory/generate")
                        .param("providerId", "1")
                        .param("customerId", String.valueOf(testAccount.getAccountId()))
                        .cookie(new Cookie("jwt", jwt)))
                .andExpect(status().isForbidden());
    }


    /** tests user registration flow using dependencies
     *
     * @throws Exception Exception
     */
    @Test
    void registerUserFlow() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("screenName", "happyUser")
                        .param("password", "happyPass123")
                        .param("confirmPassword", "happyPass123")
                        .param("firstName", "Happy")
                        .param("lastName", "User")
                        .param("email", "happy@email.com")
                        .param("phoneNumber", "5551234567")
                        .param("street", "Happy St")
                        .param("city", "Joyville")
                        .param("state", "HP")
                        .param("zipCode", "12345")
                )
                .andExpect(status().is3xxRedirection());
    }



    /** tests user login flow using dependencies
     *
     * @throws Exception Exception
     */
    @Test
    void loginUserFlow() throws Exception {

        mockMvc.perform(post("/auth/register")
                .param("screenName", "happyUser")
                .param("password", "happyPass123")
                .param("confirmPassword", "happyPass123")
                .param("firstName", "Happy")
                .param("lastName", "User")
                .param("email", "happy@email.com")
                .param("phoneNumber", "5551234567")
                .param("street", "Happy St")
                .param("city", "Joyville")
                .param("state", "HP")
                .param("zipCode", "12345"));

        mockMvc.perform(post("/auth/login")
                        .param("screenName", "happyUser")
                        .param("password", "happyPass123"))
                .andExpect(status().isOk());
    }




    /** tests refresh token flow using dependencies
     *
     * @throws Exception Exception
     */
    @Test
    void refreshTokenFlow() throws Exception {
        // Register and log in to get refresh token
        mockMvc.perform(post("/auth/register")
                        .param("screenName", "refreshUser")
                        .param("password", "refreshPass123")
                        .param("confirmPassword", "refreshPass123")
                        .param("firstName", "Refresh")
                        .param("lastName", "User")
                        .param("email", "refresh@email.com")
                        .param("phoneNumber", "5551234567")
                        .param("street", "Refresh St")
                        .param("city", "Tokenville")
                        .param("state", "RF")
                        .param("zipCode", "54321"));

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .param("screenName", "refreshUser")
                        .param("password", "refreshPass123")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andReturn();

        Cookie refreshCookie = Arrays.stream(loginResult.getResponse().getCookies())
                .filter(c -> "refreshToken".equals(c.getName()))
                .findFirst()
                .orElseThrow();

        // call refresh endpoint with refresh token cookie
        mockMvc.perform(post("/auth/refresh")
                        .cookie(refreshCookie)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection());
    }



    /** tests user logout flow using dependencies
     *
     * @throws Exception Exception
     */
    @Test
    void logoutFlow() throws Exception {

        // register
        mockMvc.perform(post("/auth/register")
                        .param("screenName", "happyUser")
                        .param("password", "happyPass123")
                        .param("confirmPassword", "happyPass123")
                        .param("firstName", "Happy")
                        .param("lastName", "User")
                        .param("email", "happy@email.com")
                        .param("phoneNumber", "5551234567")
                        .param("street", "Happy St")
                        .param("city", "Joyville")
                        .param("state", "HP")
                        .param("zipCode", "12345"));

        // login
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .param("screenName", "happyUser")
                        .param("password", "happyPass123")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andReturn();

        // get accountId
        Account account = accountRepository.findByScreenName("happyUser").orElseThrow();
        int accountId = account.getAccountId();

        // logout
        mockMvc.perform(post("/auth/logout")
                        .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isOk());
    }



}
