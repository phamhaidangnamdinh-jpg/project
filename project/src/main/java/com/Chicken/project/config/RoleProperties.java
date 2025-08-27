package com.Chicken.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Properties;

@Component
@EnableMethodSecurity
public class RoleProperties {
    private final Properties properties = new Properties();
    public RoleProperties(@Value("classpath:role.properties") Resource resource) throws IOException {
        properties.load(resource.getInputStream());
    }

    public String getRequiredPermission(String path){
        return properties.getProperty(path);
    }
}
