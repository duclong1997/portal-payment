package com.ezpay.main.authen.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private String connectId;
    private String connectKey;
    private boolean active;
    private List<Role> roles;

    public UserPrincipal() {
    }

    public UserPrincipal(String connectId, String connectKey, boolean active, List<Role> roles) {
        this.connectId = connectId;
        this.connectKey = connectKey;
        this.active = active;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return connectKey;
    }

    @Override
    public String getUsername() {
        return connectId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
