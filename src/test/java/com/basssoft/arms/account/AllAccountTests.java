package com.basssoft.arms.account;

import com.basssoft.arms.account.controller.AccountControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.account.service.AccountSvcTest;

@Suite
@SelectClasses({
        AccountSvcTest.class,
        AccountControllerTest.class
})
public class AllAccountTests {

}
