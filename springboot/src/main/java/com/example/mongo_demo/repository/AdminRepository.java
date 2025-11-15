package com.example.mongo_demo.repository;
import com.example.mongo_demo.entity.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Description :
 * @Author :lenovo
 * @Date :2021/6/1 10:54
 */
@Repository
public interface AdminRepository extends MongoRepository<Admin,String> {

    Admin save(Admin admin);

    // 根据条件查询所有（对应原selectAll）
    List<Admin> findAll();

    // 插入单个文档（对应原insert）
    Admin insert(Admin admin);

    // 根据用户名查询（对应原selectByUsername）
    @Query("{'username': ?0}") // MongoDB查询语法，?0对应第一个参数
    Admin findByUsername(String username);

    // 根据ID查询用户名（对应原selectUsername）
    @Query(value = "{'_id': ?0}", fields = "{'username': 1, '_id': 0}") // 只返回username字段
    String findUsernameById(String id);

    // 根据ID删除（对应原deleteById）
    @Query(value = "{'_id': ?0}")
    void deleteById(String id);

    // 根据ID查询（对应原selectById）
    @Query("{'_id': ?0}")
    Admin findAdminById(String id);

}
