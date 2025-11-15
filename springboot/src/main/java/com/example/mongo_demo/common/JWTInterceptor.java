package com.example.mongo_demo.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.service.AdminService;
import com.example.mongo_demo.service.StudentService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Resource
    AdminService adminService;
    @Resource
    StudentService studentService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头拿到token
        String token = request.getHeader("token");
        if (StrUtil.isEmpty(token)) {
            // 如果没拿到，从参数里再拿一次
            token = request.getParameter("token");
        }
        // 2. 认证token
        if (StrUtil.isBlank(token)) {
            throw new CustomerException("401", "您无权操作");
        }
        Account account = null;

        try {
            // 拿到token 的负荷数据
            String audience = JWT.decode(token).getAudience().get(0);
            String[] split = audience.split("-");
            String id = split[0];
            String role = split[1];
            // 根据token解析出来的userId 去对应的表查询用户信息
            if ("ADMIN".equals(role)) {
                account = adminService.findAdminById(id);
            } else if ("STUDENT".equals(role)) {
                account = studentService.findStudentById(id);
            } else {
                throw new CustomerException("404", "出现错误");
            }
        } catch (Exception e) {
            throw new CustomerException("401", "您无权操作");
        }

        if (account == null) {
            throw new CustomerException("401", "您无权操作");
        }

        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new CustomerException("401", "您无权操作");
        }
        return true;
    }
}
