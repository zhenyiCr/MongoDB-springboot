package com.example.mongo_demo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "student")
public class Student extends Account {
    @Field("major")
    private String major;
    @Field("grade")
    private String grade;
    @Field("college")
    private String college;

    //非数据库属性
    private String ids;
    private List<String> idArr;
    private String token;

    // 新增社团相关字段
    private String clubRole; // 社团内角色：LEADER/MEMBER/null
    private String clubId;   // 所属社团ID：xxx/null
}
