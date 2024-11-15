package com.example.crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Student Class를 DB table에 mapping.
@Entity
public class Student {
    @Id // id feild를 primary key로 지정
    @GeneratedValue // id 값 자동 생성, 
    (strategy = GenerationType.IDENTITY) // DB가 ID를 자동 증가 시켜 생성하는 방식

    private Long id;
    private String name;
    private String major;
    private String phone;
    private String address;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return this.major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }







}
