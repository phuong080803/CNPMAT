package com.example.cdcnpmat.Model.Bean;

import javax.xml.namespace.QName;

public class Users {
    private String id;
    private String password;
    private String role;
    private String email;
    private String name;
    private String phone;

    // Constructor
    public Users(String id, String password, String role, String email, String name, String phone) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
