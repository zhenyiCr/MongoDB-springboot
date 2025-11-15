package com.example.mongo_demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notice")
@Data
public class Notice {
    @Id
    private String id;
    @Field("title")
    private String title;
    @Field("content")
    private String content;
    @Field("time")
    private String time;
}
