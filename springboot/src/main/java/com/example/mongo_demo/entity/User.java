package com.example.mongo_demo.entity;


import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user")
public class User extends Account{


    //非数据库属性
    private String ids;
    private List<String> idArr;


    public String getIds() {
        return ids;
    }
    public void setIds(String ids) {
        this.ids = ids;
    }

    public void setIdArr(List<String> idArr) {
        this.idArr = idArr;
    }
    public List<String> getIdArr() {
        return idArr;
    }

}
