package com.example.mongo_demo.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.User;
import com.example.mongo_demo.service.UserService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/suser")
    public Result user(String username) {
        String user = userService.User(username);
        return Result.success(user);
    }

    @PostMapping("/add")
    public Result add(@RequestBody User user) { // @RequestBody 接受前端传来的json数据
        userService.add(user);
        return Result.success();
    }
//    @DeleteMapping("/delete/{id}")
//    public Result delete(@PathVariable Integer id) { // @PathVariable 接受路径参数
//        userService.deleteById(id);
//        return Result.success();
//    }
//    @DeleteMapping("/deleteBatch")
//    public Result deleteBatch(@RequestBody List<User> list) {
//        userService.deleteBatch(list);
//        return Result.success();
//    }
//    @PutMapping("/update")
//    public Result update(@RequestBody User user) {
//        userService.update(user);
//        return Result.success();
//    }
//
//    @GetMapping("/selectAll")
//    public Result slectAll(User user) {
//        List<User> userList = userService.selectAll(user);
//        return Result.success(userList);
//    }
//
//    // 分页查询
//    // pageNum 当前页数
//    // pageSize 每页显示的条数
//    @GetMapping("/selectPage")
//    public Result slectPage(@RequestParam(defaultValue = "1") Integer pageNum,
//                            @RequestParam(defaultValue = "10") Integer pageSize,
//                            User user) {
//        PageInfo<User> pageInfo = userService.selectPage(pageNum,pageSize,user);
//        return Result.success(pageInfo);
//    }
//
//    // excel 导出
//    @GetMapping("/export")
//    public void exportDate(User user, HttpServletResponse response) throws Exception {
//        String ids =user.getIds();
//        if  (StrUtil.isNotBlank(ids)) {
//            String[] split = ids.split(",");
//            user.setIdArr(split);
//        }
//        // 1. 拿到所有数据
//        List<User> list = userService.selectAll(user);
//        // 2. 构建writer对象
//        ExcelWriter writer = ExcelUtil.getWriter(true);
//        // 3. 设置中文表头
//        writer.addHeaderAlias("username", "账号");
//        writer.addHeaderAlias("name", "用户");
//        writer.addHeaderAlias("phone", "手机");
//        writer.addHeaderAlias("email", "邮箱");
//
//        // 默认的 未添加alias的属性也会写出 如果想只写出加了别名的字段 可以调用此方法排除之
//        writer.setOnlyAlias(true);
//        // 4. 写出数据到wrriter
//        writer.write(list);
//        // 5. 设置输出的文件的名称以及输出流的表头信息
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
//        String fileName = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
//        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//        //6. 写出到输出流 并关闭writer
//        ServletOutputStream os = response.getOutputStream();
//        writer.flush(os);
//        writer.close();
//        os.close();
//    }
//
//    // Excel导入
//    @PostMapping("/import")
//    public Result importData(MultipartFile file) throws Exception {
//        // 1. 拿到输入流 构建 reader
//        InputStream inputStream = file.getInputStream();
//        ExcelReader reader = ExcelUtil.getReader(inputStream);
//        // 2. 通过Reader读取 excel 里面的数据
//        reader.addHeaderAlias("账号", "username");
//        reader.addHeaderAlias("用户", "name");
//        reader.addHeaderAlias("手机", "phone");
//        reader.addHeaderAlias("邮箱", "email");
//        List<User> list = reader.readAll(User.class);
//        // 3. 批量插入数据库
//        for (User user : list) {
//            userService.add(user);
//        }
//        return Result.success();
//    }
}
