package com.example.mongo_demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "club")
@Data
public class Club {
    @Id
    private String id;
    @Field("name")
    private String name;
    @Field("description")
    private String description;
    @Field("status")
    private String status;
    @Field("leaderId")
    private String leaderId;

    // 社团负责人姓名
    private String leaderName;

}
