package com.banquito.core.security.annotation;

import com.banquito.core.security.enums.RolUsuarioCoreEnum;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasAnyRole(@rolUsuarioCoreEnumProvider.getRoles(#roles))")
public @interface RequireRole {
    RolUsuarioCoreEnum[] roles();
}
