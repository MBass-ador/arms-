package com.basssoft.arms.security.exception;

/**
 *  custom exception for user not found scenarios
 *
 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class UserNotFoundException extends RuntimeException {

    /** constructor

     * @param message String
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
