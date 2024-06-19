package com.bookstore.controller;

import com.bookstore.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendEmail(@RequestParam String to,
                            @RequestParam String subject,
                            @RequestParam String body,
                            @RequestParam(required = false, defaultValue = "false") boolean isHtml) {
        if (isHtml) {
            emailService.sendHtmlEmail(to, subject, body);
        } else {
            emailService.sendSimpleEmail(to, subject, body);
        }
        return "Email sent successfully!";
    }
}
