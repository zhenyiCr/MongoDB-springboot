package com.example.mongo_demo.service;

import cn.hutool.core.date.DateUtil;
import com.example.mongo_demo.entity.*;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.ApplicationRepository;
import com.example.mongo_demo.repository.ClubMemberRepository;
import com.example.mongo_demo.repository.ClubRepository;
import com.example.mongo_demo.repository.StudentRepository;
import com.example.mongo_demo.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    @Resource
    ApplicationRepository applicationRepository;
    @Resource
    ClubRepository clubRepository;
    @Resource
    private ClubMemberRepository clubMemberRepository;
    @Resource
    StudentRepository studentRepository;


    public List<Application> selectAll(Application application) {
        // 构建动态查询条件（非null字段作为查询条件）
        Example<Application> example = Example.of(application);
        return applicationRepository.findAll(example);
    }


    public void add(Application application) {
        // 1. 获取当前登录学生ID
        String studentId = TokenUtils.getCurrentUser().getId();
        application.setStudentId(studentId);

        // 2. 校验社团是否存在（MongoDB的findById返回Optional，需处理空值）
        Optional<Club> clubOpt = clubRepository.findById(application.getClubId());
        if (clubOpt.isEmpty()) {
            throw new CustomerException("该社团不存在");
        }

        // 3. 新增校验：检查学生是否已加入任何社团
        ClubMember existingMembers = clubMemberRepository.findByStudentId(studentId);
        if (existingMembers != null) {
            throw new CustomerException("一个学生只能加入一个社团，无法提交新申请");
        }

        // 4. 新增校验：检查该学生是否已申请过该社团
        Application existing = applicationRepository.findByStudentIdAndClubId(
                studentId,
                application.getClubId()
        );
        if (existing != null) {
            throw new CustomerException("你已申请过该社团，无需重复申请");
        }

        application.setStatus("PENDING");
        application.setCreateTime(DateUtil.now());
        applicationRepository.insert(application);
    }


    // 分页查询申请（管理员/学生视角）
    public Page<Application> selectPage(Integer pageNum, Integer pageSize, Application application) {
        Account currentUser = TokenUtils.getCurrentUser();
        String userRole = currentUser.getRole();

        // 权限过滤逻辑（保持不变）
        if ("ADMIN".equals(userRole)) {
            // 管理员无限制
        } else if ("STUDENT".equals(userRole)) {
            ClubMember leader = clubMemberRepository.findByStudentIdAndRole(currentUser.getId(), "LEADER");
            if (leader != null) {
                application.setClubId(leader.getClubId());
            } else {
                application.setStudentId(currentUser.getId());
            }
        } else {
            throw new CustomerException("权限不足，无法查看申请");
        }

        // 分页查询申请列表
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Example<Application> example = Example.of(application);
        Page<Application> applicationPage = applicationRepository.findAll(example, pageable);
        List<Application> applications = applicationPage.getContent();

        if (!applications.isEmpty()) {
            // 1. 收集所有需要关联的ID
            Set<String> studentIds = applications.stream()
                    .map(Application::getStudentId)
                    .collect(Collectors.toSet());
            Set<String> clubIds = applications.stream()
                    .map(Application::getClubId)
                    .collect(Collectors.toSet());

            // 2. 批量查询学生和社团信息
            Map<String, Student> studentMap = studentRepository.findAllById(studentIds).stream()
                    .collect(Collectors.toMap(Student::getId, Function.identity()));
            Map<String, Club> clubMap = clubRepository.findAllById(clubIds).stream()
                    .collect(Collectors.toMap(Club::getId, Function.identity()));

            // 3. 填充关联字段
            for (Application app : applications) {
                // 填充学生姓名
                Student student = studentMap.get(app.getStudentId());
                if (student != null) {
                    app.setStudentName(student.getName());
                }
                // 填充社团名称
                Club club = clubMap.get(app.getClubId());
                if (club != null) {
                    app.setClubName(club.getName());
                }
            }
        }

        return new PageImpl<>(applications, pageable, applicationPage.getTotalElements());
    }


    public void deleteById(String id) {
        applicationRepository.deleteById(id);
    }


    public void deleteBatch(List<Application> list) {
        for (Application application : list) {
            this.deleteById(application.getId());
        }
    }

    // 审核申请权限校验
    private void checkApprovePermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 系统管理员：拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色：需校验是否为该社团的社长（LEADER）
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询该学生在申请的社团中是否为社长
            ClubMember leaderMember = clubMemberRepository.findByStudentIdAndClubId(
                    currentUser.getId(), clubId
            );
            if (leaderMember == null || !"LEADER".equals(leaderMember.getRole())) {
                throw new CustomerException("无权限审核该社团申请");
            }
        } else {
            // 其他角色：无权限
            throw new CustomerException("权限不足，无法审核申请");
        }
    }


    // 审核申请（同意/拒绝）
    public void approveApplication(String applicationId, String status) {
        // 1. 查询申请详情（获取申请的社团ID，处理Optional空值）
        Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
        if (applicationOpt.isEmpty()) {
            throw new CustomerException("申请不存在");
        }
        Application application = applicationOpt.get();
        String clubId = application.getClubId(); // 申请的社团ID

        // 2. 校验审核权限
        checkApprovePermission(clubId);

        // 3. 执行审核（更新状态）
        application.setStatus(status);
        application.setApproverId(TokenUtils.getCurrentUser().getId());
        application.setApproveTime(DateUtil.now());
        applicationRepository.save(application);
    }
}
