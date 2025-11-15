package com.example.mongo_demo.entity;


// 标记为映射超类，仅用于继承，不生成独立集合

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class Account {
    @Id
    private String id;
    @Field("username")
    private String username;
    @Field("password")
    private String password;
    @Field("name")
    private String name;
    @Field("role")
    private String role;
    @Field("avatar")
    private String avatar;

}
