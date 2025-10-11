package com.example.mongo_demo.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.Admin;
import com.example.mongo_demo.service.AdminService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin")
    public Result admin(String adminname) {
        String admin = adminService.Admin(adminname);
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
    public Result selectAll() {
        List<Admin> adminList = adminService.selectAll();
        return Result.success(adminList);
    }


    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            Admin admin
            ) {
//        Page<Admin> page = adminService.findAll(pageNum, pageSize, admin);
//        return Result.success(page);
        PageImpl<Admin> all = adminService.findAll(pageNum, pageSize, admin);
        ResponseEntity<Page<Admin>> ok = ResponseEntity.ok(all);
        return Result.success(ok);
    }

    // excel 导出
    @GetMapping("/export")
    public void exportDate(Admin admin, HttpServletResponse response) throws Exception {
        String ids =admin.getIds();
        if  (StrUtil.isNotBlank(ids)) {
            String[] split = ids.split(",");
            admin.setIdArr(Arrays.asList( split));
        }
        // 1. 拿到所有数据
        List<Admin> list = adminService.selectById(admin.getIdArr());
        // 2. 构建writer对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 3. 设置中文表头
        writer.addHeaderAlias("adminname", "账号");
        writer.addHeaderAlias("name", "用户");
        writer.addHeaderAlias("phone", "手机");
        writer.addHeaderAlias("email", "邮箱");

        // 默认的 未添加alias的属性也会写出 如果想只写出加了别名的字段 可以调用此方法排除之
        writer.setOnlyAlias(true);
        // 4. 写出数据到wrriter
        writer.write(list);
        // 5. 设置输出的文件的名称以及输出流的表头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        //6. 写出到输出流 并关闭writer
        ServletOutputStream os = response.getOutputStream();
        writer.flush(os);
        writer.close();
        os.close();
    }

    // Excel导入
    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {
        // 1. 拿到输入流 构建 reader
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 2. 通过Reader读取 excel 里面的数据
        reader.addHeaderAlias("账号", "adminname");
        reader.addHeaderAlias("用户", "name");
        reader.addHeaderAlias("手机", "phone");
        reader.addHeaderAlias("邮箱", "email");
        List<Admin> list = reader.readAll(Admin.class);
        // 3. 批量插入数据库
        for (Admin admin : list) {
            adminService.add(admin);
        }
        return Result.success();
    }
}
