package com.bookstore.entity;

// src/main/java/com/example/email/Email.java

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emailto")  // Đổi tên cột từ "to" thành "recipient"
    private String to;
    private String subject;
    private String body;
    private Date sentDate;
    private String status;

    // Getters and setters
    // Constructor
    public Email() {}

    public Email(String to, String subject, String body, Date sentDate, String status) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.sentDate = sentDate;
        this.status = status;
    }
}
