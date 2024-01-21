package org.example.config.security;

import org.example.repository.UserRepository;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User storedUser = userRepository.findByUsername(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(storedUser.getUsername())
                .password(storedUser.getPassword())
                .accountExpired(!storedUser.isActive())
                .build();
    }
}
