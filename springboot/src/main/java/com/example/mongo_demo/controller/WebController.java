package com.example.mongo_demo.controller;

import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.ChangePasswordDTO;
import com.example.mongo_demo.entity.Student;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.service.AdminService;
import com.example.mongo_demo.service.StudentService;
import com.example.mongo_demo.utils.TokenUtils;
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
    StudentService studentService;
    @GetMapping("/E")
    public Result hello() {
        return Result.success("hello world");
    }

    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account dbAccount = null;
        if (account.getRole().equals("ADMIN")) {
            dbAccount = adminService.login(account);
        } else if (account.getRole().equals("STUDENT")) {
            dbAccount = studentService.login(account);
        } else {
            throw new CustomerException("用户角色错误");
        }
        return Result.success(dbAccount);
    }
    @PostMapping("/register")
    public Result register(@RequestBody Student student) {
        studentService.register(student);
        return Result.success();
    }

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        // 从token中获取当前登录用户（无需前端传递account）
        Account currentAccount = TokenUtils.getCurrentUser();
        // 验证新密码和确认密码一致性
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            throw new CustomerException("新密码和确认密码不一致");
        }
        Account dbAccount = null;
        if (currentAccount.getRole().equals("ADMIN")) {
            dbAccount = adminService.updatePassword(changePasswordDTO, currentAccount);
        } else if (currentAccount.getRole().equals("STUDENT")) {
            dbAccount = studentService.updatePassword(changePasswordDTO, currentAccount);
        } else {
            throw new CustomerException("用户角色错误");
        }
        return Result.success(dbAccount);
    }
}
