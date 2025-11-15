package com.example.mongo_demo.entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "club_members")
@Data
public class ClubMember {
    @Id
    private String id;
    @Field("club_id")
    private String clubId;
    @Field("student_id")
    private String studentId;
    @Field("role")
    private String role; // 社团内角色：MEMBER（普通成员）、LEADER（社团管理员）
    @Field("join_time")
    private String joinTime;

    // 非数据库字段，用于前端显示
    private String clubName;
    private String studentName;
    private String studentUsername;
}
