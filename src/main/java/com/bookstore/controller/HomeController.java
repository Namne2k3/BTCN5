package com.bookstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String home(Model model){
        try {
            return "home/index";

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error/500";
        }
    }
}
