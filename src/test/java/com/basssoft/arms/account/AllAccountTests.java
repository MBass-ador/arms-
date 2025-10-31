package com.basssoft.arms.account;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.basssoft.arms.account.service.AccountSvcTest;

@Suite
@SelectClasses({
        AccountSvcTest.class
})
public class AllAccountTests {

}
