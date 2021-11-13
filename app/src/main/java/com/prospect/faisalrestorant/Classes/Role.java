package com.prospect.faisalrestorant.Classes;

public class Role {
    public  String key;
    public   String  role;

    Role() {

    }
    public  Role(
            String key,
            String role
    ) {
        this.key=key;
        this.role=role;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
