package com.basssoft.arms.account.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
    private Integer accountId;

    @Column(nullable = false, unique = true, length = 24)
    private String screenName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ToString.Exclude
    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false)
    private boolean provider;

    @Column(nullable = false, length = 24)
    private String firstName;

    @Column(nullable = false, length = 24)
    private String lastName;

    @Column(nullable = false, unique = true, length = 48)
    private String email;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 24)
    private String street;

    @Column(nullable = false, length = 24)
    private String city;

    @Column(nullable = false, length = 24)
    private String state;

    @Column(nullable = false, length = 10)
    private String zipCode;
}
