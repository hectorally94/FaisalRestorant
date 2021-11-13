package com.prospect.faisalrestorant.Classes;

public class User {
    String key;
    String email;
    String password;
    String fullname;
String role;
    public User(){

    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    public User(String key,
                String user,
                String email,
                String password,
                String Role

    ) {
        this.key=key;
        this.fullname=user;
        this.email=email;
        this.password=password;
        this.role=Role;
    }

}
