package com.basssoft.arms.account.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object
 * for Account data

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private int accountId;

    private String screenName;
    private String password;
    private boolean isProvider;

    private String firstName;
    private String lastName;

    private String email;
    private String phoneNumber;

    private String street;
    private String city;
    private String state;
    private String zipCode;
}
