package com.bookstore.services;

import com.bookstore.dto.ReqResUser;
import com.bookstore.entity.User;
import com.bookstore.repository.IRoleRepository;
import com.bookstore.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    public User getUserById(Integer userId) {
        return userRepository.findById(Integer.valueOf(userId)).get();
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUserS();
    }

    public void save(User user) {
        userRepository.save(user);
        Long userId = userRepository.getUserIdByUserName(user.getUsername());
        Long roleId = roleRepository.getRoleIdByName("USER");
        if ( roleId != null && userId != 0 ) {
            userRepository.addRoleToUser(userId, roleId);
        }
    }

    public User getUserByEmail (String email) {
        return userRepository.getUserByEmail(email);
    }
}
