package model;

import java.util.Date;

public class Employee {
    private int employeeId;
    private String username;
    private String passwordHash;
    private String name;
    private String contact;
    private Date hireDate;
    private int roleId;

    public Employee(int employeeId, String username, String passwordHash, String name, String contact, Date hireDate, int roleId) {
        this.employeeId = employeeId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.contact = contact;
        this.hireDate = hireDate;
        this.roleId = roleId;
    }

    // Getters and setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
