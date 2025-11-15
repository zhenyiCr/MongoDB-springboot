package com.example.mongo_demo.controller;

import com.example.mongo_demo.entity.Application;
import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.service.ApplicationService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Resource
    ApplicationService applicationService;


    @PostMapping("/add")
    public Result add(@RequestBody Application application) { // @RequestBody 接受前端传来的json数据
        applicationService.add(application);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        applicationService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Application> list) {
        applicationService.deleteBatch(list);
        return Result.success();
    }


    @GetMapping("/selectAll")
    public Result selectAll(Application application) {
        List<Application> applicationList = applicationService.selectAll(application);
        return Result.success(applicationList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Application application) {
        Page<Application> pageInfo = applicationService.selectPage(pageNum,pageSize,application);
        return Result.success(pageInfo);
    }

    // 审核申请（更新状态）
    @PutMapping("/approve")
    public Result approve(@RequestBody Application application) {
        applicationService.approveApplication(application.getId(), application.getStatus());
        return Result.success();
    }

}
