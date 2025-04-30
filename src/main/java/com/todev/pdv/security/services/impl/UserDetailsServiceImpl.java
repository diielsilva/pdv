package com.todev.pdv.security.services.impl;

import com.todev.pdv.core.providers.contracts.UserProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserProvider userProvider;

    public UserDetailsServiceImpl(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return userProvider
                .findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("O usuário: %s não foi encontrado!", login)));
    }
}
