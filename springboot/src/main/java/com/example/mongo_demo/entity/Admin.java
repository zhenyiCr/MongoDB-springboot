package com.example.mongo_demo.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import lombok.Data;

@Data
@Document(collection = "admin")
public class Admin extends Account{


    //非数据库属性
    private String ids;
    private List<String> idArr;
    private String token;
}
