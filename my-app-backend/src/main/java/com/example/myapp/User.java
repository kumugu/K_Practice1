package com.example.myapp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;

    public void setPassword(String encode) {
    }

    public String getEmail() {
        return "";
    }

    public void setEmail(String email) {
    }

    public String getPassword() {
        return "";
    }
}
