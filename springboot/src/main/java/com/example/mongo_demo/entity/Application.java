package com.example.mongo_demo.entity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "application")
@Data
public class Application {
    private String id;
    private String studentId;
    private String clubId;
    private String status;
    private String reason;
    private String approverId;
    private String createTime;
    private String approveTime;
    private String remark;

    // 非数据库字段，用于前端显示
    private String studentName;
    private String clubName;
}
