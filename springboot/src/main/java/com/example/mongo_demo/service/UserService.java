package com.example.mongo_demo.service;

import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.User;
import com.example.mongo_demo.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Query;
import com.example.mongo_demo.exception.CustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // 注入MongoTemplate用于构建动态查询
    @Autowired
    private MongoTemplate mongoTemplate;

    public String User(String username) {
        if (username.equals("user"))
            return username;
        else throw new CustomerException("用户名不存在");
    }

    public List<User> selectAll(User user) {
        return userRepository.findAll();
    }

    public void add(User user) {
        // 判断用户名是否已存在
        User dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser != null) {
            throw new CustomerException("账号已存在");
        }
        user.setRole("USER");
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("user");
        }
        userRepository.save(user);
    }

    public PageImpl<User> findAll(int pageNum, int pageSize,User user) {
        // 1. 构建查询条件
        Criteria criteria = new Criteria();
        // 如果username不为空，添加精确匹配条件
        if (StrUtil.isNotBlank(user.getUsername())) {
            criteria.and("username").regex(user.getUsername(),"i");
            //criteria.and("username").is(user.getUsername()); // 精确匹配条件
        }
        // 如果name不为空，添加模糊匹配条件
        if (StrUtil.isNotBlank(user.getName())) {
            criteria.and("name").regex(user.getName(), "i"); // "i"表示忽略大小写
        }
        Query query = new Query(criteria);

        // 2. 计算总条数
        long total = mongoTemplate.count(query, User.class);

        // 1. 构建 Pageable 对象 (使用 PageRequest)
        // ** 非常重要：页码是从 0 开始的！**
        Pageable pageable = PageRequest.of(pageNum-1, pageSize);
        query.with(pageable); // 给查询添加分页参数

        // 4. 查询当前页数据
        List<User> users = mongoTemplate.find(query, User.class);

        return new PageImpl<>(users, pageable, total);

    }

//    public void update(User user) {
//        if (!userMapper.selectUsername(user).equals(user.getUsername())) {
//            User dbUser = userMapper.selectByUsername(user.getUsername());
//            if (dbUser != null) {
//                throw new CustomerException("账号已存在");
//            }
//        }
//        userMapper.update(user);
//
//    }
//
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    public void deleteBatch(List<User> list) {
        for (User user : list) {
            this.deleteById(user.getId());
        }
    }

//    public User login(User user) {
//        User dbUser = userMapper.selectByUsername(user.getUsername());
//        if (dbUser == null) {
//            throw new CustomerException("用户名不存在");
//        }
//        if (!dbUser.getPassword().equals(user.getPassword())) {
//            throw new CustomerException("账号或密码错误");
//        }
//        return dbUser;
//    }
}
