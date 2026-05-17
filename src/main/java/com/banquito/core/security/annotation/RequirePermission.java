package com.banquito.core.security.annotation;

import com.banquito.core.security.enums.PermisoEnum;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasAnyAuthority(@permisoEnumProvider.getPermissions(#permissions))")
public @interface RequirePermission {
    PermisoEnum[] permissions();
}
