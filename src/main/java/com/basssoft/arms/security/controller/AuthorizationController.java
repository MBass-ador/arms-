package com.basssoft.arms.security.controller;

import com.basssoft.arms.account.domain.AccountDTO;
import com.basssoft.arms.account.service.IaccountService;
import com.basssoft.arms.security.exception.InvalidRefreshTokenException;
import com.basssoft.arms.security.exception.UserNotFoundException;
import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import com.basssoft.arms.security.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;
import jakarta.servlet.http.Cookie;

/**
 * AuthorizationController
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@Controller
@RequestMapping("/auth")
public class AuthorizationController {

    // dependencies
    private final JWTService jwtService;
    private final ArmsUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final IaccountService accountService;



    /** constructor
     *  injects dependencies

     * @param jwtService JWTService
     * @param userDetailsService ArmsUserDetailsService
     * @param refreshTokenService RefreshTokenService
     */
    public AuthorizationController(
            JWTService jwtService,
            ArmsUserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            IaccountService accountService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }


    /** show login form

     * @return login view
     */
    @GetMapping("/login")
    public String showLoginForm() {

        // return login view
        return "security/Login";
    }


    /** go to registration form

     * @param model Model
     * @return register view
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        // fresh dto for form
        model.addAttribute("account", new AccountDTO());
        // edit mode enables form fields
        model.addAttribute("editMode", true);
        return "security/Registration";
    }


    /** handle login form submission

     * @param screenName String
     * @param password String
     * @return redirect to SPA view or back to log-in on failure
     */
    @PostMapping(value = "/login", consumes = "application/x-www-form-urlencoded")
    public String login(
            @RequestParam("screenName") String screenName,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        // get Account by screen name (throws if not found)
        var account = userDetailsService.getAccountByScreenName(screenName)
            .orElseThrow(() ->
            new UserNotFoundException("User not found: " + screenName));

        // verify password
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // load UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(screenName);

        // generate JWT
        String jwtToken = jwtService.generateToken(userDetails, Map.of("accountId", account.getAccountId()));

        // create refresh token
        var refreshToken = refreshTokenService.createRefreshToken(account.getAccountId());

        // save JWT to browser as HTTP-only cookie
        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // not using HTTPS in dev
        jwtCookie.setPath("/");    //  cookie for all paths
        jwtCookie.setMaxAge(60 * 60); // 1 hour expiration
        response.addCookie(jwtCookie);

        System.out.println("response with jwtCookie: " + jwtCookie);

        // save Refresh Token to browser as HTTP-only cookie
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // not using HTTPS in dev
        refreshCookie.setPath("/");    //  cookie for all paths
        refreshCookie.setMaxAge(24 * 60 * 60); // 1 day expiration
        response.addCookie(refreshCookie);

        // redirect to SPA with accountId
        return "redirect:/ArmsSPA/" + account.getAccountId();
    }




    /** handle token refresh

     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return redirect to SPA view
     */
    @PostMapping(value = "/refresh", consumes = "application/x-www-form-urlencoded")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {

        // extract refreshToken from cookies
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        // check for refresh token
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidRefreshTokenException("Refresh token missing");
        }
        // validate refresh token
        var refreshTokenEntity = refreshTokenService.getByToken(refreshToken);
        Integer accountId = refreshTokenEntity.getAccountId();

        // load user details
        var account = accountService.getAccount(accountId);
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getScreenName());

        // generate JWT
        String jwt = jwtService.generateToken(userDetails, Map.of("accountId", accountId));

        // put new JWT in cookie
        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // not using HTTPS in dev
        jwtCookie.setPath("/");     // cookie for all paths
        jwtCookie.setMaxAge(60 * 60); // 1 hour
        response.addCookie(jwtCookie);

        // redirect to SPA
        return "redirect:/ArmsSPA/" + accountId;
    }



    /** handle logout

     * @param accountId Integer
     * @return redirect to login view
     */
    @PostMapping("/logout")
    public String logout(
            @RequestParam("accountId") Integer accountId,
            HttpServletResponse response
    ) {
        // delete refresh tokens for account
        refreshTokenService.deleteByAccountId(accountId);

        // remove JWT and refreshToken cookies
        Cookie jwtCookie = new Cookie("jwt", "");
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        // redirect to login
        return "redirect:/auth/login";
    }




    /** handle registration form submission

     * @param account AccountDTO
     * @param bindingResult BindingResult
     * @param redirectAttributes RedirectAttributes
     * @param model Model
     * @return redirect to registration view
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute AccountDTO account,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model)  {

        // debug
        System.out.println("Register called");
        System.out.println("AccountDTO: " + account);

        // check password confirmation
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            if (!account.getPassword().equals(account.getConfirmPassword())) {
                System.out.println("Password and confirmPassword do not match");
                bindingResult.rejectValue("confirmPassword",
                                      "error.confirmPassword", "Passwords do not match.");
            }
        }

        // check for validation errors
        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors found:");
            model.addAttribute("account", account);
            model.addAttribute("editMode", true);
            return "security/Registration";
        }

        // encode password w BCrypt
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        // create account
        System.out.println("No validation errors, proceeding to create account");
        AccountDTO createdAccount = accountService.createAccount(account);

        System.out.println("Account created: " + createdAccount);

        // success message
        redirectAttributes.addFlashAttribute("message",
                                             "Registration Successful: you can now log in!");

        // redirect to login page
        return "redirect:/auth/login";
    }


}
