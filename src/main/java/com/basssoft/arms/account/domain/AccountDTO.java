package com.basssoft.arms.account.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object
 * for Account data

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Integer accountId;

    @NotBlank
    @Size(min = 8, max = 20)
    private String screenName;

    @NotBlank
    @Size(min = 6, max = 50)
    @JsonIgnore
    private String password;

    @NotNull
    private boolean provider;

    @NotBlank
    @Size(max = 24)
    private String firstName;

    @NotBlank
    @Size(max = 24)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 50)
    private String street;

    @NotBlank
    @Size(max = 24)
    private String city;

    @NotBlank
    @Size(max = 2)
    private String state;

    @NotBlank
    @Size(max = 10)
    private String zipCode;
}
