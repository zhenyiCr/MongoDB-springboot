package com.example.mongo_demo.controller;


import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.User;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.service.AdminService;
import com.example.mongo_demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebController {
    @Resource
    AdminService adminService;
    @Resource
    UserService userService;
    @GetMapping("/E")
    public Result hello() {
        return Result.success("hello world");
    }

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account dbAccount = null;
        if (account.getRole().equals("ADMIN")) {
            dbAccount = adminService.login(account);
        } else if (account.getRole().equals("USER")) {
            dbAccount = userService.login(account);
        } else {
            throw new CustomerException("用户角色错误");
        }
        return Result.success(dbAccount);
    }
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        userService.register(user);
        return Result.success();
    }

}
