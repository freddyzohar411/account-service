package com.avensys.rts.accountservice.annotation;

import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAllRoles {
    Role[] value() default {Role.USER};
}