package com.bookstore.controller;

import com.bookstore.entity.User;
import com.bookstore.repository.IUserRepository;
import com.bookstore.services.EmailService;
import com.bookstore.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login(Model model) {
        try {
            return "user/login";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        try {
            var userList = userServices.getAllUsers();
            model.addAttribute("users", userList);
            return "user/list";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @GetMapping("/recovery_password")
    public String recoveryPassword() {
        return "user/recovery_password";
    }

    @PostMapping("/recovery_password")
    public String recoveryPassword_post(@Valid @ModelAttribute("email") String email, Model model) {
        try {
            var user = userServices.getUserByEmail(email);
            if ( user != null ) {
                emailService.sendSimpleEmail(email,"khoi phuc mau khau","http://localhost:8080/submit_recovery_password/" +  user.getId());
                return "sent";
            }
            model.addAttribute("error", "Email chưa được đăng ký");
            return "user/recovery_password";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại sau.");
            return "user/recovery_password";
        }

    }

    @GetMapping("/submit_recovery_password/{userId}")
    public String recoveryPasswordSubmit(@PathVariable("userId") Integer userId, @Valid @ModelAttribute("email") String email, BindingResult bindingResult, Model model) {
        try {
            model.addAttribute("userId", userId);
            return "user/submit_recovery_password";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/submit_recovery_password")
    public String submit_recoveryPasswordSubmit(@ModelAttribute("userId") String userId, @ModelAttribute("password") String password,@ModelAttribute("re_password") String re_password, BindingResult bindingResult, Model model) {
        try {
            var user = userServices.getUserById(Integer.valueOf(userId));
            if ( user.getId() != null ) {
                if ( password.equals(re_password) ) {
                    user.setPassword(new BCryptPasswordEncoder().encode(re_password));
                    userServices.updateUser(user);
                    return "user/login";
                }
            }
            return "user/submit_recovery_password";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }
    @GetMapping("/register")
    public String register(Model model) {
        try {
            model.addAttribute("user", new User());
            return "user/register";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        try {
            if ( bindingResult.hasErrors() ) {
                List<FieldError> errors = bindingResult.getFieldErrors();
                for( FieldError erro : errors ) {
                    model.addAttribute(erro.getField() + "_error", erro.getDefaultMessage());
                }
                return "user/register";
            }

            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
            userServices.save(user);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }
}
