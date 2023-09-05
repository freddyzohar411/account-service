package com.avensys.rts.accountservice.annotation;


import com.avensys.rts.accountservice.validators.URLOrNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = URLOrNullValidator.class)
@Documented
public @interface URLOrNull {
    String message() default "Invalid URL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}