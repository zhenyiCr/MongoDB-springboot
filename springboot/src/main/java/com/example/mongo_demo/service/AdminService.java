package com.example.mongo_demo.service;

import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.Admin;
import com.example.mongo_demo.entity.ChangePasswordDTO;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.AdminRepository;
import com.example.mongo_demo.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    public List<Admin> selectAll(Admin admin) {
        return adminRepository.findAll();
    }
    public List<Admin> selectById(List<String> id) {
        return adminRepository.findAllById(id);
    }
    public Admin findAdminById(String id) {
        return adminRepository.findAdminById(id);
    }

    public void add(Admin admin) {
        // 判断用户名是否已存在
        Admin dbAdmin = adminRepository.findByUsername(admin.getUsername());
        if (dbAdmin != null) {
            throw new CustomerException("账号已存在");
        }
        admin.setRole("ADMIN");
        if (StrUtil.isBlank(admin.getPassword())) {
            admin.setPassword("admin");
        }
        adminRepository.save(admin);
    }

    // 返回类型改为 Page<Admin>（接口类型）
    public Page<Admin> selectPage(int pageNum, int pageSize, Admin admin) {
        // 1. 构建查询条件（支持模糊查询）
        Criteria criteria = new Criteria();
        // 用户名模糊查询（忽略大小写）
        if (StrUtil.isNotBlank(admin.getUsername())) {
            criteria.and("username").regex(admin.getUsername(), "i");
        }
        // 姓名模糊查询（忽略大小写）
        if (StrUtil.isNotBlank(admin.getName())) {
            criteria.and("name").regex(admin.getName(), "i");
        }
        Query query = new Query(criteria);

        // 2. 计算符合条件的总条数
        long total = mongoTemplate.count(query, Admin.class);

        // 3. 构建分页参数（MongoDB页码从0开始，需减1）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        query.with(pageable); // 为查询添加分页参数（跳过前面的记录+限制每页条数）

        // 4. 查询当前页数据
        List<Admin> admins = mongoTemplate.find(query, Admin.class);

        // 5. 返回 Page 接口的实现类 PageImpl（向上转型为 Page<Admin>）
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

    public Admin updatePassword(ChangePasswordDTO changePasswordDTO, Account currentAccount) {
        Admin dbAdmin = adminRepository.findById(currentAccount.getId()).orElse(null);
        // 正确：用用户输入的oldPassword对比数据库密码
        if (!dbAdmin.getPassword().equals(changePasswordDTO.getOldPassword())) {
            throw new CustomerException("原密码错误");
        }
        dbAdmin.setPassword(changePasswordDTO.getNewPassword());
        // 用新密码重新生成token（关键：密码变更后旧token失效）
        dbAdmin.setToken(TokenUtils.createToken(dbAdmin.getId() + "-ADMIN", dbAdmin.getPassword()));
        adminRepository.save(dbAdmin);
        return dbAdmin;
    }
}
