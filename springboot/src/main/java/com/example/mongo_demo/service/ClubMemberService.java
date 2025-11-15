package com.example.mongo_demo.service;

import cn.hutool.core.date.DateUtil;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.Club;
import com.example.mongo_demo.entity.ClubMember;
import com.example.mongo_demo.entity.Student;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.ClubMemberRepository;
import com.example.mongo_demo.repository.ClubRepository;
import com.example.mongo_demo.repository.StudentRepository;
import com.example.mongo_demo.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ClubMemberService {
    // 替换Mapper为MongoDB的Repository
    @Resource
    private ClubMemberRepository clubMemberRepository;
    @Resource
    private ClubRepository clubRepository;
    @Resource
    private StudentRepository studentRepository;

    // 管理员和社长 操作权限校验（逻辑不变，仅调整数据访问层调用）
    private void checkMemberPermission(ClubMember clubMember) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 系统管理员拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色需要校验是否为当前社团的LEADER
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前用户在该社团的角色（使用Repository的findByStudentId）
            ClubMember currentMember = clubMemberRepository.findByStudentIdAndClubId(
                    currentUser.getId(), clubMember.getClubId()
            );
            // 校验：必须是本社团的LEADER才能操作
            if (currentMember == null
                    || !"LEADER".equals(currentMember.getRole())
                    || !currentMember.getClubId().equals(clubMember.getClubId())) {
                throw new CustomerException("权限不足，无法管理该社团成员");
            }
        } else {
            // 其他角色（如未定义角色）无权限
            throw new CustomerException("权限不足，无法管理成员");
        }
    }

    // 根据社团ID查询成员（调用Repository的findByClubId）
    public List<ClubMember> getMembersByClubId(String clubId) {
        return clubMemberRepository.findByClubId(clubId);
    }

    // 分页查询社团成员（替换PageHelper为Spring Data MongoDB的分页）
    public Page<ClubMember> selectPage(Integer pageNum, Integer pageSize, ClubMember clubMember) {
        // 1. 执行原分页查询（获取ClubMember基础数据）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Account currentUser;
        try {
            currentUser = TokenUtils.getCurrentUser();
        } catch (CustomerException e) {
            throw new CustomerException("获取当前用户失败: " + (e.getMessage() != null ? e.getMessage() : "未登录或登录已过期"));
        }

        Example<ClubMember> example;
        if ("ADMIN".equals(currentUser.getRole())) {
            example = Example.of(clubMember);
        } else if ("STUDENT".equals(currentUser.getRole())) {
            ClubMember currentMember = clubMemberRepository.findByStudentId(currentUser.getId());
            if (currentMember == null) {
                throw new CustomerException("权限不足，仅社团成员可查看本社团成员列表");
            }
            clubMember.setClubId(currentMember.getClubId());
            example = Example.of(clubMember);
        } else {
            throw new CustomerException("权限不足，无法查看成员列表");
        }
        Page<ClubMember> memberPage = clubMemberRepository.findAll(example, pageable);


        // 2. 关联查询Club和Student信息，补充到ClubMember中
        List<ClubMember> members = memberPage.getContent();
        if (!members.isEmpty()) {
            // 2.1 收集所有需要查询的clubId和studentId（去重，减少查询次数）
            Set<String> clubIds = members.stream().map(ClubMember::getClubId).collect(Collectors.toSet());
            Set<String> studentIds = members.stream().map(ClubMember::getStudentId).collect(Collectors.toSet());

            // 2.2 批量查询Club和Student
            Map<String, Club> clubMap = clubRepository.findAllById(clubIds).stream()
                    .collect(Collectors.toMap(Club::getId, Function.identity()));
            Map<String, Student> studentMap = studentRepository.findAllById(studentIds).stream()
                    .collect(Collectors.toMap(Student::getId, Function.identity()));

            // 2.3 为每个ClubMember补充关联信息
            for (ClubMember member : members) {
                // 补充社团名称
                Club club = clubMap.get(member.getClubId());
                if (club != null) {
                    member.setClubName(club.getName());
                }
                // 补充学生姓名和学号（假设Student的username字段是学号）
                Student student = studentMap.get(member.getStudentId());
                if (student != null) {
                    member.setStudentName(student.getName());
                    member.setStudentUsername(student.getUsername()); // 学生学号
                }
            }
        }

        // 3. 返回处理后的分页结果
        return new PageImpl<>(members, pageable, memberPage.getTotalElements());
    }

    // 修改：添加成员方法，增加多维度校验
    public void addMember(ClubMember clubMember) {
        String clubId = clubMember.getClubId();
        String studentId = clubMember.getStudentId();

        // 1. 校验操作人权限
        checkAddMemberPermission(clubId);

        // 2. 校验社团是否存在（关联club集合）
        Optional<Club> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            throw new CustomerException("社团不存在，无法添加成员");
        }

        // 3. 校验学生是否存在（关联student集合）
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new CustomerException("学生不存在，无法添加为成员");
        }

        // 4. 校验学生是否已加入任何社团（一个学生只能加入一个社团）
        ClubMember existingMember = clubMemberRepository.findByStudentId(studentId);
        if (existingMember != null) {
            throw new CustomerException("该学生已加入其他社团，无法重复加入");
        }

        // 5. 校验是否已加入当前社团（防重复添加）
        ClubMember existingInClub = clubMemberRepository.findByStudentIdAndClubId(studentId, clubId);
        if (existingInClub != null) {
            throw new CustomerException("该学生已加入此社团，无需重复添加");
        }

        // 6. 校验添加"社长"角色时是否已存在社长（一个社团只能有一个社长）
        if ("LEADER".equals(clubMember.getRole())) {
            List<ClubMember> leaders = clubMemberRepository.findByClubIdAndRole(clubId, "LEADER");
            if (!leaders.isEmpty()) {
                throw new CustomerException("该社团已存在社长，无法重复设置");
            }
            // 同步更新社团的leaderId（关联club集合）
            Club club = clubOpt.get();
            club.setLeaderId(studentId); // 将社团的leaderId设为当前学生ID
            clubRepository.save(club);
        }

        // 7. 默认角色为普通成员（如果未指定）
        if (clubMember.getRole() == null) {
            clubMember.setRole("MEMBER");
        }
        // 8. 设置加入时间并保存（关联club_member集合）
        clubMember.setJoinTime(DateUtil.now());
        clubMemberRepository.insert(clubMember);
    }

    // 移除社团成员（处理MongoDB的findById返回Optional）
    public void removeMember(String id) {
        // 校验权限：查询成员信息（MongoDB的findById返回Optional，需处理空值）
        Optional<ClubMember> clubMemberOpt = clubMemberRepository.findById(id);
        if (clubMemberOpt.isEmpty()) {
            throw new CustomerException("该成员不存在");
        }
        ClubMember clubMember = clubMemberOpt.get();
        checkMemberPermission(clubMember);
        clubMemberRepository.deleteById(id);
    }

    // 修改：更新角色前校验权限（调用Repository的updateRole）
    public void updateMemberRole(ClubMember clubMember) {
        // 校验权限（先确认成员存在）
        Optional<ClubMember> existingMemberOpt = clubMemberRepository.findById(clubMember.getId());
        if (existingMemberOpt.isEmpty()) {
            throw new CustomerException("该成员不存在");
        }
        checkMemberPermission(clubMember);
        // 调用Repository的更新方法
        clubMemberRepository.updateRoleById(clubMember.getId(), clubMember.getRole());
    }

    // 添加成员的权限校验（仅管理员或社团社长可操作）
    private void checkAddMemberPermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 系统管理员拥有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生需为该社团的社长（LEADER）
        if ("STUDENT".equals(currentUser.getRole())) {
            ClubMember leader = clubMemberRepository.findByStudentIdAndClubId(currentUser.getId(), clubId);
            if (leader == null || !"LEADER".equals(leader.getRole())) {
                throw new CustomerException("无权限添加成员，仅社团社长或管理员可操作");
            }
        } else {
            throw new CustomerException("权限不足，无法添加成员");
        }
    }
}
