package com.basssoft.arms.account;

import com.basssoft.arms.account.controller.AccountControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.account.service.AccountSvcTest;

/**
 * test suite to run all account-related tests
 *
 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Suite
@SelectClasses({
        AccountSvcTest.class,
        AccountControllerTest.class
})
public class AllAccountTests {

}
