package com.example.mongo_demo.repository;
import com.example.mongo_demo.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Description :
 * @Author :lenovo
 * @Date :2021/6/1 10:54
 */
@Repository
public interface AdminRepository extends MongoRepository<Admin,String> {

    Admin findByUsername(String username);
    Admin findAdminsById(String id);

    Admin save(Admin admin);

}
