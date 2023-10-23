package com.avensys.rts.accountservice.annotation;

import com.avensys.rts.accountservice.enums.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAnyPermission {
    //    String value() default "ADMIN";
    Permission[] value() default {Permission.READ, Permission.WRITE, Permission.DELETE, Permission.EDIT};
}

