package com.example.mongo_demo.service;

import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.User;
import com.example.mongo_demo.repository.UserRepository;

import com.example.mongo_demo.exception.CustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        User dbUser = userRepository.findByUsername(user.getUsername());
        if (dbUser != null) {
            throw new CustomerException("账号已存在");
        }
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword("user");
        }
        userRepository.save(user);
    }

    public Page<User> findAllUsers(int pageNum, int pageSize) {
        // 1. 构建 Pageable 对象 (使用 PageRequest)
        // ** 非常重要：页码是从 0 开始的！**
        Pageable pageable = PageRequest.of(pageNum-1, pageSize);
        // 2. 调用 Repository 的分页方法
        Page<User> userPage = userRepository.findAll(pageable);

        // 4. 返回 Page 对象，它包含了数据和分页元数据
        System.out.println("Total Elements: " + userPage.getTotalElements());
        System.out.println("Total Pages: " + userPage.getTotalPages());
        System.out.println("Current Page Number: " + userPage.getNumber());
        System.out.println("Page Size: " + userPage.getSize());
        System.out.println("Users on current page: " + userPage.getContent());

        return userPage;
    }

    public Page<User> getUserByPage(Pageable pageable) {
        // 直接调用 Repository 内置的 findAll(Pageable)
        return userRepository.findAll(pageable);
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
