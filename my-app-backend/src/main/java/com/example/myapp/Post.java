package com.example.myapp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    public String getTitle() {
        return getTitle();
    }

    public String getContent() {
        return null;
    }

    public void setTitle(String title) {
    }

    public void setContent(String content) {
    }
}
