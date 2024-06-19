package com.bookstore.entity;

import com.bookstore.validator.ValidUsername;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    @Size(max = 50, message = "Ten dang nhap phai it hon 50 ky tu")
    @ValidUsername
    private String username ;

    @Column(name = "password" , length = 250, nullable = false)
    @NotBlank(message = "Mat khau khong duoc de trong")
    private String password;

    @Column(name = "email", length = 50)
    @Size(max = 50, message = "Email pha it hon 50 ky tu")
    private String email;

    @Column(name = "name", length = 50, nullable = false)
    @Size(max = 50,message = "Ten cua ban phai it honw 50 ky tu")
    @NotBlank(message = "TEn cua ban khong duoc de trong")
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Book> books;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
