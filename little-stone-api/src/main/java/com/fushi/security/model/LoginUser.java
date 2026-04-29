package com.fushi.security.model;

import com.fushi.entity.Permission;
import com.fushi.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LoginUser implements UserDetails {
    private User user;
    private List<String> roles;
    private List<String> permissions;
    private List<Permission> menuTree;
    private Collection<? extends GrantedAuthority> authorities;

    public LoginUser(User user, List<String> roles, List<String> permissions, List<Permission> menuTree, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
        this.menuTree = menuTree;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
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

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }

    public User getUser() {
        return user;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public List<Permission> getMenuTree() {
        return menuTree;
    }

}
