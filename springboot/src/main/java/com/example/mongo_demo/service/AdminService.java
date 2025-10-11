package com.example.mongo_demo.service;

import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.Admin;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.AdminRepository;
import com.example.mongo_demo.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    // 注入MongoTemplate用于构建动态查询
    @Autowired
    private MongoTemplate mongoTemplate;

    public String Admin(String username) {
        if (username.equals("admin"))
            return username;
        else throw new CustomerException("用户名不存在");
    }

    public List<Admin> selectAll() {
        return adminRepository.findAll();
    }
    public List<Admin> selectById(List<String> id) {
        return adminRepository.findAllById(id);
    }
    public Admin findAdminsById(String id) {
        return adminRepository.findAdminsById(id);
    }

    public void add(Admin admin) {
        // 判断用户名是否已存在
        Admin dbAdmin = adminRepository.findByUsername(admin.getUsername());
        if (dbAdmin != null) {
            throw new CustomerException("账号已存在");
        }
        admin.setRole("USER");
        if (StrUtil.isBlank(admin.getPassword())) {
            admin.setPassword("admin");
        }
        adminRepository.save(admin);
    }

    public PageImpl<Admin> findAll(int pageNum, int pageSize,Admin admin) {
        // 1. 构建查询条件
        Criteria criteria = new Criteria();
        // 如果adminname不为空，添加精确匹配条件
        if (StrUtil.isNotBlank(admin.getUsername())) {
            criteria.and("username").regex(admin.getUsername(),"i");
            //criteria.and("username").is(admin.getUsername()); // 精确匹配条件
        }
        // 如果name不为空，添加模糊匹配条件
        if (StrUtil.isNotBlank(admin.getName())) {
            criteria.and("name").regex(admin.getName(), "i"); // "i"表示忽略大小写
        }
        Query query = new Query(criteria);

        // 2. 计算总条数
        long total = mongoTemplate.count(query, Admin.class);

        // 1. 构建 Pageable 对象 (使用 PageRequest)
        // ** 非常重要：页码是从 0 开始的！**
        Pageable pageable = PageRequest.of(pageNum-1, pageSize);
        query.with(pageable); // 给查询添加分页参数

        // 4. 查询当前页数据
        List<Admin> admins = mongoTemplate.find(query, Admin.class);

        return new PageImpl<>(admins, pageable, total);

    }

    public void update(Admin admin) {

        adminRepository.save(admin);

    }

    public void deleteById(String id) {
        adminRepository.deleteById(id);
    }

    public void deleteBatch(List<Admin> list) {
        for (Admin admin : list) {
            this.deleteById(admin.getId());
        }
    }


    public Admin login(Account account) {
        Admin dbAdmin = adminRepository.findByUsername(account.getUsername());
        if (dbAdmin == null) {
            throw new CustomerException("用户名不存在");
        }
        if (!dbAdmin.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }
        // 创建token并返回给前端
        String token = TokenUtils.createToken(dbAdmin.getId() + "-" +"ADMIN", dbAdmin.getPassword());
        dbAdmin.setToken(token);
        return dbAdmin;
    }
}
