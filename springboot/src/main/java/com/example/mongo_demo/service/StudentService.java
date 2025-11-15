package com.example.mongo_demo.service;

import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.*;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.ClubMemberRepository;
import com.example.mongo_demo.repository.StudentRepository;
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
public class StudentService {
    @Autowired
    StudentRepository studentRepository;
    // 注入MongoTemplate用于构建动态查询
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    ClubMemberRepository clubMemberRepository;


    public List<Student> selectAll(Student student) {
        return studentRepository.findAll();
    }
    public void add(Student student) {
        // 判断用户名是否已存在
        Student dbStudent = studentRepository.findByUsername(student.getUsername());
        if (dbStudent != null) {
            throw new CustomerException("账号已存在");
        }
        if (StrUtil.isBlank(student.getPassword())) {
            student.setPassword("student");
        }
        if (StrUtil.isBlank(student.getName())) {
            student.setName(student.getUsername());
        }
        student.setRole("STUDENT");
        studentRepository.save(student);
    }


    public PageImpl<Student> selectPage(Integer pageNum, Integer pageSize, Student student) {
        // 1. 构建查询条件
        Criteria criteria = new Criteria();
        // 如果adminname不为空，添加精确匹配条件
        if (StrUtil.isNotBlank(student.getUsername())) {
            criteria.and("username").regex(student.getUsername(),"i");
            //criteria.and("username").is(student.getUsername()); // 精确匹配条件
        }
        // 如果name不为空，添加模糊匹配条件
        if (StrUtil.isNotBlank(student.getName())) {
            criteria.and("name").regex(student.getName(), "i"); // "i"表示忽略大小写
        }
        Query query = new Query(criteria);

        // 2. 计算总条数
        long total = mongoTemplate.count(query, Student.class);

        // 1. 构建 Pageable 对象 (使用 PageRequest)
        // ** 非常重要：页码是从 0 开始的！**
        Pageable pageable = PageRequest.of(pageNum-1, pageSize);
        query.with(pageable); // 给查询添加分页参数

        // 4. 查询当前页数据
        List<Student> students = mongoTemplate.find(query, Student.class);

        return new PageImpl<>(students, pageable, total);

    }


    public void update(Student student) {
        // 1. 校验待更新的学生是否存在
        Student existingStudent = studentRepository.findStudentById(student.getId());
        if (existingStudent == null) {
            throw new CustomerException("该学生不存在，无法更新");
        }

        // 2. 校验用户名是否修改，若修改则检查是否已被占用
        String originalUsername = existingStudent.getUsername();
        String newUsername = student.getUsername();
        if (!originalUsername.equals(newUsername)) { // 用户名有修改
            Student userWithNewUsername = studentRepository.findByUsername(newUsername);
            if (userWithNewUsername != null) { // 新用户名已被其他学生占用
                throw new CustomerException("用户名已存在，请更换");
            }
        }

        // 3. 基于已有实体更新字段（避免覆盖未传递的字段）
        existingStudent.setUsername(newUsername); // 更新用户名
        existingStudent.setName(student.getName()); // 更新姓名
        existingStudent.setMajor(student.getMajor()); // 更新专业
        existingStudent.setGrade(student.getGrade()); // 更新年级
        existingStudent.setCollege(student.getCollege()); // 更新学院
        // ... 其他需要更新的字段（根据业务需求补充）

        // 4. 保存更新后的实体
        studentRepository.save(existingStudent);
    }

    public void deleteById(String id) {
        studentRepository.deleteById(id);
    }

    public void deleteBatch(List<Student> list) {
        for (Student student : list) {
            this.deleteById(student.getId());
        }
    }

    public Student login(Account account) {
        Student dbStudent = studentRepository.findByUsername(account.getUsername());
        if (dbStudent == null) {
            throw new CustomerException("用户名不存在");
        }
        if (!dbStudent.getPassword().equals(account.getPassword())) {
            throw new CustomerException("账号或密码错误");
        }

        // 新增：查询学生的社团信息
        ClubMember clubMember = clubMemberRepository.findByStudentId(dbStudent.getId());
        if (clubMember != null) {
            dbStudent.setClubId(clubMember.getClubId());
            dbStudent.setClubRole(clubMember.getRole());
        } else {
            dbStudent.setClubId(null);
            dbStudent.setClubRole(null);
        }

        String token = TokenUtils.createToken(dbStudent.getId() + "-" +"STUDENT", dbStudent.getPassword());
        dbStudent.setToken(token);
        return dbStudent;
    }

    public void register(Student student) {
        this.add(student);
    }

    public Student selectById(String id) {
        return studentRepository.findStudentById(id);
    }

    public Student updatePassword(ChangePasswordDTO changePasswordDTO, Account currentAccount) {
        Student dbStudent = studentRepository.findStudentById(currentAccount.getId());
        // 正确：用用户输入的oldPassword对比数据库密码
        if (!dbStudent.getPassword().equals(changePasswordDTO.getOldPassword())) {
            throw new CustomerException("原密码错误");
        }
        dbStudent.setPassword(changePasswordDTO.getNewPassword());
        // 用新密码重新生成token（关键：密码变更后旧token失效）
        dbStudent.setToken(TokenUtils.createToken(dbStudent.getId() + "-STUDENT", dbStudent.getPassword()));
        studentRepository.save(dbStudent);
        return dbStudent;
    }

    public Student findStudentById(String id) {
        return studentRepository.findStudentById(id);
    }
}
