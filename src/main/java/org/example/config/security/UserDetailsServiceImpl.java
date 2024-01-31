package org.example.config.security;

import org.example.exception.notfound.UserNotFoundException;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .map(
                        storedUser -> org.springframework.security.core.userdetails.User.builder()
                                .username(storedUser.getUsername())
                                .password(storedUser.getPassword())
                                .accountExpired(!storedUser.isActive())
                                .accountLocked(false)
                                .build()
                )
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }
}
