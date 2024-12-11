package com.example.cdcnpmat.Model.Bean;

public class Users {
    private String id;

    private String password;
    private String role;
    private String email;

    // Constructor
    public Users(String id, String password, String role, String email) {
        this.id = id;
        this.password = password;
        this.role = role;
        this.email = email;
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



    public String getRoleId() {
        return role;
    }

    public void setRoleId(String roleId) {
        this.role = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
