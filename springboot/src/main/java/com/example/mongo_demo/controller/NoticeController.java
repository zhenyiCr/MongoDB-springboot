package com.example.mongo_demo.controller;

import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.Notice;
import com.example.mongo_demo.service.NoticeService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    NoticeService noticeService;

    @GetMapping("/notice")
    public Result notice(String title) {
        String notice = noticeService.Notice(title);
        return Result.success(notice);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Notice notice) { // @RequestBody 接受前端传来的json数据
        noticeService.add(notice);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        noticeService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Notice> list) {
        noticeService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Notice notice) {
        noticeService.update(notice);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Notice notice) {
        List<Notice> noticeList = noticeService.selectAll(notice);
        return Result.success(noticeList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Notice notice) {
        Page<Notice> pageInfo = noticeService.selectPage(pageNum,pageSize,notice);
        return Result.success(pageInfo);
    }

}
