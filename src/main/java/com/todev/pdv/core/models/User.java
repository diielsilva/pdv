package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    private Integer id;
    private String name;
    private String login;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isUserValid();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isUserValid();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isUserValid();
    }

    @Override
    public boolean isEnabled() {
        return isUserValid();
    }

    private boolean isUserValid() {
        return deletedAt == null;
    }
}
