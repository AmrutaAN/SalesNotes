package com.salesnotes.security;

import com.salesnotes.entity.User;
import com.salesnotes.entity.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username;
    private final String fullname;
    private final String email;
    private final String password;
    private final UserRole role;
    private final String organisationId;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
        this.organisationId = user.getOrganisation() != null ? user.getOrganisation().getId() : null;
        this.active = Boolean.TRUE.equals(user.getActive()) && !Boolean.TRUE.equals(user.getDeleted());
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + (user.getRole() != null ? user.getRole().name() : "COMPANYEMPLOYEE"))
        );
    }

    public String getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
