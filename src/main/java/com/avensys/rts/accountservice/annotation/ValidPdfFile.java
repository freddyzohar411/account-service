package com.avensys.rts.accountservice.annotation;

import com.avensys.rts.accountservice.validators.ValidPdfFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: Koh He Xiang
 * Custom pdf file format validator annotation class
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPdfFileValidator.class)
public @interface ValidPdfFile {
    String message() default "Invalid file format. Only PDF files are allowed.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}