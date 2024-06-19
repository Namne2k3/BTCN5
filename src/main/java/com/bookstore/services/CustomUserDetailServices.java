package com.bookstore.services;

import com.bookstore.entity.CustomUserDetail;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.repository.IRoleRepository;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class CustomUserDetailServices implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if ( user == null ) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetail(user, userRepository);
    }

    public User processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Check if the user exists in your database
        User user = userRepository.getUserByEmail(email);

        if (user == null) {
            // Create a new user if not exists
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setUsername(email);

            String randomPassword = generateRandomPassword();
            // Set the password for the new user
            user.setPassword(passwordEncoder.encode(randomPassword));

            userRepository.save(user);
            Long userId = userRepository.getUserIdByUserName(user.getUsername());


            Long roleId = roleRepository.getRoleIdByName("USER");
            if ( roleId != null && userId != 0 ) {
                userRepository.addRoleToUser(userId,roleId);
            }

            // Save the new user to the database
            return user;
        }

        return user;
    }

    private String generateRandomPassword() {
        // Implement your logic to generate a random password here
        // Example implementation (not secure, just for demonstration)
        return UUID.randomUUID().toString().substring(0, 8); // Generate a random 8-character password
    }
}
