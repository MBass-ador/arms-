package com.basssoft.arms.account.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Account
 * domain entity class

 * arms application
 * @author Matthew Bass
 * @version 2.0
 */
@Getter
@Setter
@Entity
@Table(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @NotBlank
    @Size(min = 8, max = 20)
    @Column(name = "screen_name", nullable = false, unique = true, length = 24)
    private String screenName;

    @NotBlank
    @Size(min = 6, max = 60)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false)
    private boolean provider;

    @NotBlank
    @Size(max = 24)
    @Column(name= "first_name", nullable = false, length = 24)
    private String firstName;

    @NotBlank
    @Size(max = 24)
    @Column(name= "last_name", nullable = false, length = 24)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank
    @Size(max = 20)
    @Column(name= "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String street;

    @NotBlank
    @Size(max = 24)
    @Column(nullable = false, length = 24)
    private String city;

    @NotBlank
    @Size(max = 2)
    @Column(nullable = false, length = 2)
    private String state;

    @NotBlank
    @Size(max = 10)
    @Column(name= "zip_code", nullable = false, length = 10)
    private String zipCode;
}
