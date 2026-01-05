package com.todev.pdv.core.models;

import com.todev.pdv.core.enums.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "users")
public class User extends BaseModel implements UserDetails {
    @Id
    private Integer id;
    private String name;
    private String login;
    private String password;
    private Role role;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return getLogin();
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
        return getDeletedAt() == null;
    }
}
