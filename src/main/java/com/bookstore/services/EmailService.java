package com.bookstore.services;

import com.bookstore.entity.Email;
import com.bookstore.repository.EmailRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailRepository emailRepository;

    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setSentDate(new Date());

            mailSender.send(message);

            saveEmail(to, subject, body, "SENT");
        } catch (Exception e) {
            saveEmail(to, subject, body, "FAILED");
            e.printStackTrace();
        }
    }

    public void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setSentDate(new Date());

            mailSender.send(message);

            saveEmail(to, subject, body, "SENT");
        } catch (Exception e) {
            saveEmail(to, subject, body, "FAILED");
            e.printStackTrace();
        }
    }

    private void saveEmail(String to, String subject, String body, String status) {
        Email email = new Email(to, subject, body, new Date(), status);
        emailRepository.save(email);
    }
}
