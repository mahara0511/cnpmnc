package com.example.restservice.service;

import com.example.restservice.entity.User;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.Collections;
import com.example.restservice.entity.Supervisor;
import com.example.restservice.entity.Employee;
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new CustomUserDetails(user, getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String role = "ROLE_USER";
        
        if (user instanceof Supervisor) {
            role = "ROLE_SUPERVISOR";
        } else if (user instanceof Employee) {
            role = "ROLE_EMPLOYEE";
        }
        
        log.debug("User {} assigned role: {}", user.getEmail(), role);
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
