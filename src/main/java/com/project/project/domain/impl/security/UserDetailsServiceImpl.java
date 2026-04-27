package com.project.project.domain.impl.security;

import com.project.project.domain.repo.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userJpaRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(username + " not found." );
        }
        Set<GrantedAuthority> authorities = user.get()
                .getRoles()
                .stream()
                .map((role) -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                authorities
        );
    }
}
