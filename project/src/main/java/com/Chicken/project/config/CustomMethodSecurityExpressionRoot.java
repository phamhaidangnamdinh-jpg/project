package com.Chicken.project.config;

import com.Chicken.project.entity.UserPrincipal;
import com.Chicken.project.entity.V_User;
import com.Chicken.project.service.impl.BookServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private final RoleProperties roleProperties;
    private final V_User user;
    private Object filterObject;
    private Object returnObject;
    private Object target;
    private static final Logger log =  LoggerFactory.getLogger(CustomMethodSecurityExpressionRoot.class);
    public CustomMethodSecurityExpressionRoot(Authentication auth, RoleProperties roleProperties) {
        super(auth);
        this.roleProperties = roleProperties;
        this.user = ((UserPrincipal) auth.getPrincipal()).getUser();
    }
    @Override public Object getFilterObject() { return filterObject; }
    @Override public void setFilterObject(Object filterObject) { this.filterObject = filterObject; }
    @Override public Object getReturnObject() { return returnObject; }
    @Override public void setReturnObject(Object returnObject) { this.returnObject = returnObject; }
    @Override public Object getThis() { return target; }
    public void setThis(Object target) { this.target = target; }

    public boolean fileRole(HttpServletRequest request){
        String normalizedPath = normalizePath(request.getRequestURI());
        String requiredPermission = roleProperties.getRequiredPermission(normalizedPath);
//        System.out.println("permissions: "+ user.getRoleGroup().getFunctions().stream().toString());
//        System.out.println(requiredPermission);
//        System.out.println("Checking permission for URI: " + request.getRequestURI() + " | Normalized: " + normalizedPath);
        log.info("Checking permission for URI: '{}' | Normalized: ''{}", request.getRequestURI(), normalizedPath);
        if (requiredPermission == null) {
//            System.out.println("No permission mapping found in role.properties for path: " + normalizedPath);
            log.warn("No permission mapping found in role.properties for path: '{}'", normalizedPath);
            return false;
        }

        return user.getRoleGroup().getFunctions()
                .stream().anyMatch(f -> f.getFunctionCode().equals(requiredPermission));
    }

    private String normalizePath(String path){
        String[] parts = path.split("/");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank() || part.matches("\\d+") || part.matches("[0-9a-fA-F\\-]{36}")) continue;
            sb.append(".").append(part);
        }
        return sb.length() > 0 ? sb.substring(1) : "";
    }

}
