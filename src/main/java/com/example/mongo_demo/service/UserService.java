package com.example.mongo_demo.service;

import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.User;
import com.example.mongo_demo.repository.UserRepository;

import exception.CustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

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
        User dbUser = userRepository.findAllByUsername(user.getUsername());
        if (dbUser != null) {
            throw new CustomerException("账号已存在");
        }
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("user");
        }
        userRepository.save(user);
    }


//    public PageInfo<User> selectPage(Integer pageNum, Integer pageSize, User user) {
//        // 开始分页查询
//        PageHelper.startPage(pageNum, pageSize);
//        List<User> users = userMapper.selectAll(user);
//        return PageInfo.of(users);
//    }
//
//
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
//    public void deleteById(String id) {
//        userMapper.deleteById(id);
//    }
//
//    public void deleteBatch(List<User> list) {
//        for (User user : list) {
//            this.deleteById(user.getId());
//        }
//    }
//
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
