package com.basssoft.arms.security;

import com.basssoft.arms.security.config.JwtAuthenticationFilterTest;
import com.basssoft.arms.security.service.ArmsUserDetailsServiceTest;
import com.basssoft.arms.security.service.JWTServiceTest;
import com.basssoft.arms.security.service.RefreshTokenServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * test suite for all security tests
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
@Suite
@SelectClasses({
        ArmsUserDetailsServiceTest.class,
        RefreshTokenServiceTest.class,
        JWTServiceTest.class,
        JwtAuthenticationFilterTest.class,
        SecurityIntegrationTests.class
})
public class AllSecurityTests {
}
