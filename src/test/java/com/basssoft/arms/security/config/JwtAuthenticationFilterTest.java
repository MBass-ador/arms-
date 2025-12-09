package com.basssoft.arms.security.config;

import com.basssoft.arms.security.service.ArmsUserDetailsService;
import com.basssoft.arms.security.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *  test class for JwtAuthenticationFilter
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class JwtAuthenticationFilterTest {

    //dependencies
    private JWTService jwtService;
    private ArmsUserDetailsService userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;


    /**
     * setup before each test
     */
    @BeforeEach
    void setUp() {
        jwtService = Mockito.mock(JWTService.class);
        userDetailsService = Mockito.mock(ArmsUserDetailsService.class);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
    }


    /**
     *  tests doFilterInternal() with no Authorization header
     */
    @Test
    void testNoAuthorizationHeader() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        //  no cookies
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        filter.doFilterInternal(request, response, filterChain);

        // assertions
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }


    /**
     *  tests doFilterInternal() with invalid JWT token
     */
    @Test
    void testInvalidJwt() throws Exception {

        // arrange:  invalid JWT cookie
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/some/protected/resource");
        request.setCookies(new Cookie("jwt", "invalid.jwt.token"));

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }


    /**
     *  tests doFilterInternal() with valid JWT token
     *  and verifies authentication is set correctly
     */
    @Test
    void testValidJwtSetsAuthentication() throws Exception {

        // arrange
        String username = "testuser";
        String jwt = "valid.jwt.token";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn(List.of());
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", jwt));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);

        // cleanup
        SecurityContextHolder.clearContext();
    }


    /**
     *  tests shouldNotFilter() method
     */
    @Test
    void shouldNotFilter_skipsAuthPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/auth/login");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        request.setServletPath("/auth/register");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        request.setServletPath("/accounts/1");
        assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));
    }

}
