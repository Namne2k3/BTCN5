package com.bookstore.dto;

import lombok.Data;


@Data
public class ReqResUser {

    private Long id;

    private String username ;


    private String email;


    private String name;


    private String role_name;
}
