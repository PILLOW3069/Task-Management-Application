package com.hr.TaskTracker.security;

import com.hr.TaskTracker.model.User;
import com.hr.TaskTracker.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomerUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getUser_id(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final Long userId;

        public CustomUserDetails(Long userId, String username, String password,
                                 Collection<? extends GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }
}
