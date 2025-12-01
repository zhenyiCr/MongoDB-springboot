package com.example.mongo_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoDemoApplication.class, args);
    }

}


//db.admin.insertOne({
//  "username": "admin",
//  "password": "admin",
//  "name": "普通管理员",
//  "role": "ADMIN"
//})