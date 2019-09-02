package com.bittech.Client.entity;


import lombok.Data;

//user表的实体类
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String brief;
}
