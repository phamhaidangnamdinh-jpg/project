package com.Chicken.project.config;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.function.Supplier;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final RoleProperties roleProperties;

    public CustomMethodSecurityExpressionHandler(RoleProperties roleProperties) {
        this.roleProperties = roleProperties;
    }
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root =
                new CustomMethodSecurityExpressionRoot(authentication, roleProperties);
        root.setThis(invocation.getThis());
        return root;
    }
    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication,
                                                     MethodInvocation invocation) {
        return createEvaluationContext(authentication.get(), invocation);
    }
}
