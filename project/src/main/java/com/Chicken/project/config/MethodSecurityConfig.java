package com.Chicken.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig  {
    private final RoleProperties roleProperties;

    public MethodSecurityConfig(RoleProperties roleProperties) {
        this.roleProperties = roleProperties;
    }

//    @Bean
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        return new CustomMethodSecurityExpressionHandler(roleProperties);
//    }
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return new CustomMethodSecurityExpressionHandler(roleProperties);
    }
}
