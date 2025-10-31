package com.basssoft.arms.account.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Account
 * domain entity class

 * arms application
 * @author Matthew Bass
 * @version 1.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Account {

    @Id
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
