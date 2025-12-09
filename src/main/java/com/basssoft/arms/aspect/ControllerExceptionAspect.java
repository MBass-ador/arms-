package com.basssoft.arms.aspect;

import com.basssoft.arms.security.exception.InvalidRefreshTokenException;
import com.basssoft.arms.security.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.NoSuchElementException;

/**
 *  aspect for handling exceptions in controller layer
 *  including exceptions returned from the service layer
 *
 * arms application
 * @author Matthew Bass
 * @version 6.0
 */
@ControllerAdvice
public class ControllerExceptionAspect {

    private static final Logger exceptionLogger = LoggerFactory.getLogger("com.basssoft.arms.exception");


    /**
     *  handle exceptions thrown in controller layer
     *  and those propagated from service layer
     *
     * @param ex Exception
     * @param model Model
     * @return error view name
     */
    @ExceptionHandler(Exception.class)
    public String handleControllerExceptions(Exception ex, Model model) {

        // error message to return (set below based on type)
        String message;

        // switch handling for known exceptions
        if (ex instanceof NoSuchElementException) {
            message = "Resource not found.";
            exceptionLogger.error("NoSuchElementException: {}", ex.getMessage());

        } else if (ex instanceof DataIntegrityViolationException) {
            message = "Invalid or incomplete data: Database constraints were violated. Please check your input.";
            exceptionLogger.error("DataIntegrityViolationException: {}", ex.getMessage());

        } else if (ex instanceof MethodArgumentTypeMismatchException) {
            message = "Invalid path or parameter type: " + ex.getMessage();
            exceptionLogger.error("MethodArgumentTypeMismatchException: {}", ex.getMessage());

        } else if (ex instanceof InvalidRefreshTokenException) {
            message = "Invalid refresh token: " + ex.getMessage();
            exceptionLogger.error("InvalidRefreshTokenException: {}", ex.getMessage(), ex);

        } else if (ex instanceof UserNotFoundException) {
            message = "User not found: " + ex.getMessage();
            exceptionLogger.error("UserNotFoundException: {}", ex.getMessage(), ex);

        } else if (ex instanceof ConstraintViolationException) {
            message = "Validation failed: One or more fields did not meet requirements. Please check your input.";
            exceptionLogger.error("ConstraintViolationException: {}", ex.getMessage(), ex);

        } else {
            message = "An unexpected error occurred.";
            exceptionLogger.error("Unexpected Exception: ", ex);
        }
        model.addAttribute("error", message);
        return "error";
    }

}
