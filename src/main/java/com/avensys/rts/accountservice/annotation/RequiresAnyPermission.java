package com.avensys.rts.accountservice.annotation;

import com.avensys.rts.accountservice.enums.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Koh He Xiang
 * This annotation is used to check if the user has any of the
 * permissions specified in the annotation
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAnyPermission {
    Permission[] value() default {};

}

