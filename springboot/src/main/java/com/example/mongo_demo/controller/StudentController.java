package com.example.mongo_demo.controller;

import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.Student;
import com.example.mongo_demo.service.StudentService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    StudentService studentService;

    @PostMapping("/add")
    public Result add(@RequestBody Student student) { // @RequestBody 接受前端传来的json数据
        studentService.add(student);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        studentService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Student> list) {
        studentService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Student student) {
        studentService.update(student);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Student student) {
        List<Student> studentList = studentService.selectAll(student);
        return Result.success(studentList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Student student) {
        PageImpl<Student> all = studentService.selectPage(pageNum, pageSize, student);
        return Result.success(all);
    }

}
