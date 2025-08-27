package com.Chicken.project.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {
    private V_User user;
    public UserPrincipal(V_User user) {
        this.user = user;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getRoleGroup() == null || user.getRoleGroup().getFunctions() == null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleGroup().getRoleGroupCode())); // fallback
        }

        return user.getRoleGroup().getFunctions().stream()
                .map(f -> new SimpleGrantedAuthority(f.getFunctionCode()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    public Long getId() {
        return user.getId();
    }
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
