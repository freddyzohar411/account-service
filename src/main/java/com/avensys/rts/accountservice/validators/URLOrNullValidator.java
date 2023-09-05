package com.avensys.rts.accountservice.validators;

import com.avensys.rts.accountservice.annotation.URLOrNull;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class URLOrNullValidator implements ConstraintValidator<URLOrNull, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Allow null values
        System.out.println("value: " + value);
        if (value.isEmpty()) {
            return true;
        }

        // Use your custom URL validation logic here
        // You can use a regular expression or a URL validation library to check if it's a valid URL

        // For example, using a simple regex pattern for demonstration purposes:
        return value.matches("^(http|https)://.*$");
    }
}