package com.example.mongo_demo.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.service.AdminService;
import com.example.mongo_demo.service.StudentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class TokenUtils {

    @Resource
    AdminService adminService;
    @Resource
    StudentService studentService;



    static AdminService staticAdminService;
    static StudentService staticStudentService;

    // springboot工程启动后会加载这段代码
    @PostConstruct
    public void init() {
        staticAdminService = adminService;
        staticStudentService = studentService;
    }

    // 生成token
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(),1))
                .sign(Algorithm.HMAC256(sign));
    }

    // 获取当前登录的用户信息
    public static Account getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getHeader("token");
        }

        // 拿到token 的负荷数据
        String audience = JWT.decode(token).getAudience().get(0);
        String[] split = audience.split("-");
        String id = split[0];
        String role = split[1];
        // 根据token解析出来的userId 去对应的表查询用户信息
        if ("ADMIN".equals(role)) {
            return staticAdminService.findAdminById(id);
        } else if ("USER".equals(role)) {
            return staticStudentService.findStudentById(id);
        } else {
            throw new CustomerException("404", "出现错误");
        }
    }
}