package com.banquito.core.security.config;

import com.banquito.core.security.filter.BranchScopeFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final BranchScopeFilter branchScopeFilter;

    public WebMvcConfig(BranchScopeFilter branchScopeFilter) {
        this.branchScopeFilter = branchScopeFilter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(branchScopeFilter)
                .addPathPatterns("/api/v1/core/cuentas/**")
                .addPathPatterns("/api/v1/core/clientes/**")
                .addPathPatterns("/api/v1/core/transacciones/**");
    }
}
