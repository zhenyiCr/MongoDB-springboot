package com.example.mongo_demo.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.exception.CustomerException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

// 文件上传下载相关的接口
@RestController
@RequestMapping("/file")
public class FileController {

    // 文件上传
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) throws Exception {
        //找到文件位置
        String filePath = System.getProperty("user.dir") + "/files/";
        if (FileUtil.isDirectory(filePath)) {
            FileUtil.mkdir(filePath);
        }
        byte[] bytes = file.getBytes();
        String filename = System.currentTimeMillis()+ "-" + file.getOriginalFilename(); // 文件的原始名称
        // 写出文件
        FileUtil.writeBytes(bytes,filePath + filename);
        String url = "http://localhost:8088/file/download/" + filename;
        return Result.success(url);
    }


    // 文件下载
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName , HttpServletResponse response) throws Exception{
        // 找到文件的位置
        String filePath = System.getProperty("user.dir") + "/files/"; // 获取当前项目的路径
        fileName = filePath + fileName; // 拼接文件名
        if (!FileUtil.exist(fileName)) {
            throw new CustomerException("文件不存在");
        }
        // 读取文件的字节流
        byte[] bytes = FileUtil.readBytes(fileName);
        ServletOutputStream os = response.getOutputStream();
        // 输出流对象把文件写到客户端
        os.write(bytes);
        os.flush();
        os.close();
    }
    @PostMapping("/wang/upload")
    public Map<String,Object> wangupload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String flag = System.currentTimeMillis() + "";
        try {
            String filePath = System.getProperty("user.dir") + "/files/";
            // 文件命名 时间戳-文件名
            FileUtil.writeBytes(file.getBytes(),filePath + flag + "-" + fileName);
            System.out.println("文件上传成功");
            Thread.sleep(1l);
        } catch (Exception e) {
            System.out.println("文件上传失败");
        }
        String http = "http://localhost:8088/file/download/" + flag + "-" + fileName;
        Map<String,Object> resMap = new HashMap<>();
        // 图片上传成功后，返回图片的url和文件名
        resMap.put("errno",0);
        resMap.put("data", CollUtil.newArrayList(Dict.create().set("url",http)));
        return resMap;
    }
}
