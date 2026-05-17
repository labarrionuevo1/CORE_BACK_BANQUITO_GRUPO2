package com.banquito.core.security.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireBranchScope {
    String parameterName() default "sucursalId";
}
