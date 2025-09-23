package com.example.mongo_demo.entity;

public class Admin {
    private String id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private String email;
    private String role;

    //非数据库属性
    private String ids;
    private String[] idArr;

    public String getIds() {
        return ids;
    }
    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setIdArr(String[] idArr) {
        this.idArr = idArr;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
