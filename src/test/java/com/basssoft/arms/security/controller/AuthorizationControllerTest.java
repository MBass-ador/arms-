package com.basssoft.arms.security.controller;

import com.basssoft.arms.account.domain.Account;
import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.security.domain.RefreshToken;
import com.basssoft.arms.account.service.IaccountService;
import com.basssoft.arms.security.exception.InvalidRefreshTokenException;
import com.basssoft.arms.security.exception.UserNotFoundException;
import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import com.basssoft.arms.security.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import java.time.Instant;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  unit tests for AuthorizationController
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@ExtendWith(MockitoExtension.class)
public class AuthorizationControllerTest {

    // mock dependencies
    @Mock
    private JWTService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ArmsUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IaccountService accountService;

    @Mock
    private HttpServletResponse response;

    // inject mocks into controller
    @InjectMocks
    private AuthorizationController authorizationController;



    /**  tests showLoginForm()
     */
    @Test
    void showLoginFormTest() {
        String viewName = authorizationController.showLoginForm();
        assertEquals("security/Login", viewName);
    }


    /**  tests showRegisterForm()
     */
    @Test
    void showRegisterFormTest() {
        Model model = new ExtendedModelMap();
        String viewName = authorizationController.showRegisterForm(model);

        assertEquals("security/Registration", viewName);
        assertTrue(model.containsAttribute("account"));
        assertNotNull(model.getAttribute("account"));
        assertTrue(model.getAttribute("account") instanceof AccountDTO);
    }


    /**  test login endpoint with valid credentials

     * @throws Exception Exception
     */
    @Test
    void loginTest_ValidCreds() throws Exception {

        // mock user details
        String username = "testuser";
        String password = "testpass";
        int accountId = 42;

        var account = new Account();
        account.setScreenName(username);
        account.setPassword(password);
        account.setAccountId(accountId);

        var userDetails = User
                .withUsername(username)
                .password(password)
                .authorities("ROLE_CUSTOMER")
                .build();

        // set up mock services
        Mockito.when(userDetailsService.getAccountByScreenName(username)).thenReturn(Optional.of(account));
        Mockito.when(passwordEncoder.matches(password, account.getPassword())).thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(jwtService.generateToken(Mockito.any(), Mockito.anyMap())).thenReturn("jwt-token");
        Mockito.when(refreshTokenService.createRefreshToken(accountId))
            .thenReturn
                (new RefreshToken(1L, "refresh-token", Instant.now().plusSeconds(3600), accountId));

        // set up redirectAttributes and model
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Model model = new ExtendedModelMap();

        // call controller method
        String result = authorizationController.login(username, password, response);

        // assert
        assertEquals("redirect:/ArmsSPA/" + accountId, result);

        Mockito.verify(response).addCookie(Mockito.argThat(cookie ->
                "jwt".equals(cookie.getName()) && "jwt-token".equals(cookie.getValue())
        ));

        Mockito.verify(response).addCookie(Mockito.argThat(cookie ->
                "refreshToken".equals(cookie.getName()) && "refresh-token".equals(cookie.getValue())
        ));
    }




    /**  test login endpoint with invalid credentials

     * @throws Exception Exception
     */
    @Test
    void loginTest_InvalidCreds() throws Exception {

        // test values
        String username = "wronguser";
        String password = "wrongpass";

        // simulate user not found
        Mockito.when(userDetailsService.getAccountByScreenName(username))
                .thenReturn(Optional.empty());

        // expect UserNotFoundException to be thrown
        assertThrows(UserNotFoundException.class,
                () -> authorizationController.login(username, password, response)
        );

        // verify no cookies set
        Mockito.verify(response, Mockito.never()).addCookie(Mockito.any());
    }



    /**  tests refreshToken() valid token

     * @throws Exception Exception
     */
    @Test
    void refreshTokenTest_ValidToken() throws Exception {

        // arrange
        String refreshTokenValue = "valid-refresh-token";
        int accountId = 123;
        String jwt = "new-jwt-token";
        String screenName = "user1";

        // mock request with refreshToken cookie
        Cookie[] cookies = {
                new jakarta.servlet.http.Cookie("refreshToken", refreshTokenValue)
        };
        HttpServletRequest request = Mockito.mock(jakarta.servlet.http.HttpServletRequest.class);
        Mockito.when(request.getCookies()).thenReturn(cookies);

        // mock refreshTokenService
        var refreshTokenEntity = Mockito.mock(com.basssoft.arms.security.domain.RefreshToken.class);
        Mockito.when(refreshTokenService.getByToken(refreshTokenValue)).thenReturn(refreshTokenEntity);
        Mockito.when(refreshTokenEntity.getAccountId()).thenReturn(accountId);

        // mock accountService
        var account = Mockito.mock(com.basssoft.arms.account.domain.AccountDTO.class);
        Mockito.when(accountService.getAccount(accountId)).thenReturn(account);
        Mockito.when(account.getScreenName()).thenReturn(screenName);

        // mock userDetailsService
        var userDetails = Mockito.mock(org.springframework.security.core.userdetails.UserDetails.class);
        Mockito.when(userDetailsService.loadUserByUsername(screenName)).thenReturn(userDetails);

        // mock jwtService
        Mockito.when(jwtService.generateToken(userDetails, Map.of("accountId", accountId))).thenReturn(jwt);

        // mock response
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // act
        String result = authorizationController.refreshToken(request, response);

        // assert
        Mockito.verify(response).addCookie(Mockito.argThat(cookie ->
                "jwt".equals(cookie.getName()) && jwt.equals(cookie.getValue())
        ));
        assertEquals("redirect:/ArmsSPA/" + accountId, result);
    }



    /**  tests refreshToken() invalid token

     * @throws Exception Exception
     */
    @Test
    void refreshTokenTest_InvalidToken() throws Exception {

        // arrange
        String invalidRefreshToken = "invalid-refresh-token";

        // mock request with invalid refreshToken
        Cookie[] cookies = { new Cookie("refreshToken", invalidRefreshToken) };
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getCookies()).thenReturn(cookies);

        // mock refreshTokenService to throw exception
        Mockito.when(refreshTokenService.getByToken(invalidRefreshToken))
                .thenThrow(new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        // mock response
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // act / assert
        assertThrows(InvalidRefreshTokenException.class, () -> {
            authorizationController.refreshToken(request, response);
        });
    }


    /**  tests logout()

     * @throws Exception Exception
     */
    @Test
    void logoutTest() throws Exception {

        Integer accountId = 42;

        // mock refresh token deletion
        Mockito.doNothing().when(refreshTokenService).deleteByAccountId(accountId);

        // mock HttpServletResponse
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        // call logout method
        String result = authorizationController.logout(accountId, response);

        // assert redirect to login
        assertEquals("redirect:/auth/login", result);

        // verify service call
        Mockito.verify(refreshTokenService).deleteByAccountId(accountId);
    }




    @Test
    void registerTest() throws Exception {
        // test data
        String username = "newuser";
        String password = "newpass";
        int accountId = 99;

        AccountDTO initialAccountDto = new AccountDTO();
        initialAccountDto.setScreenName(username);
        initialAccountDto.setPassword(password);

        AccountDTO createdAccountDto = new AccountDTO();
        createdAccountDto.setScreenName(username);
        createdAccountDto.setPassword(password);
        createdAccountDto.setAccountId(accountId);

        Mockito.when(accountService.createAccount(initialAccountDto)).thenReturn(createdAccountDto);

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Model model = new ExtendedModelMap();

        // mock BindingResult with no errors
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        // call register method (assume it takes username, password, redirectAttributes, model)
        String result = authorizationController.register(initialAccountDto, bindingResult, redirectAttributes, model);

        // assert redirect to log-in
        assertEquals("redirect:/auth/login", result);

        // verify service call
        Mockito.verify(accountService).createAccount(initialAccountDto);
    }


}
