package com.example.mongo_demo.entity;

import lombok.Data;

@Data // 用lombok简化getter/setter，也可手动生成
public class ChangePasswordDTO {
    private String oldPassword; // 旧密码（用于验证身份）
    private String newPassword; // 新密码
    private String confirmPassword; // 确认新密码（用于前端/后端二次校验）
    private String username; // 用户名（或通过token获取，避免前端传递）
}
