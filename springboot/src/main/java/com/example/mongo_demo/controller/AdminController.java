package com.example.mongo_demo.controller;

import com.example.mongo_demo.entity.Admin;
import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.service.AdminService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    AdminService adminService;

    @GetMapping("/admin")
    public Result admin(String username) {
        String admin = adminService.Admin(username);
        return Result.success(admin);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) { // @RequestBody 接受前端传来的json数据
        adminService.add(admin);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        adminService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Admin> list) {
        adminService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Admin admin) {
        adminService.update(admin);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        List<Admin> adminList = adminService.selectAll(admin);
        return Result.success(adminList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Admin admin) {
        Page<Admin> pageInfo = adminService.selectPage(pageNum, pageSize, admin);
        return Result.success(pageInfo);
    }

}
