package com.basssoft.arms.security.exception;

/**
 *  custom exception for invalid refresh tokens

 * arms application
 * @author Matthew Bass
 * @version 7.0
 */
public class InvalidRefreshTokenException extends RuntimeException {

    /** constructor

     * @param message String
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }

}
