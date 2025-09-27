package com.example.mongo_demo.repository;
import com.example.mongo_demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * @Description :
 * @Author :lenovo
 * @Date :2021/6/1 10:54
 */
@Repository
public interface UserRepository extends MongoRepository<User,String> {



    User findByUsername(String username);


}
