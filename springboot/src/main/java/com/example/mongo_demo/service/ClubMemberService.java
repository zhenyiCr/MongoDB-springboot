package com.example.mongo_demo.service;

import cn.hutool.core.date.DateUtil;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.ClubMember;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.ClubMemberRepository;
import com.example.mongo_demo.repository.ClubRepository;
import com.example.mongo_demo.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClubMemberService {
    // 替换Mapper为MongoDB的Repository
    @Resource
    private ClubMemberRepository clubMemberRepository;
    @Resource
    private ClubRepository clubRepository;

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
            ClubMember currentMember = clubMemberRepository.findByStudentId(currentUser.getId());
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
        // MongoDB分页页码从0开始，需减1
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        Account currentUser ;
        try {
            currentUser = TokenUtils.getCurrentUser();
        } catch (CustomerException e) {
            throw new CustomerException("获取当前用户失败: " + (e.getMessage() != null ? e.getMessage() : "未登录或登录已过期"));
        }

        // 1. ADMIN 权限：可查看所有社团成员（构建空条件查询）
        Example<ClubMember> example;
        if ("ADMIN".equals(currentUser.getRole())) {
            example = Example.of(clubMember); // 按传入的条件查询
        }
        // 2. STUDENT 权限：仅能查看自己所在社团的成员
        else if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前学生是否属于某个社团（使用Repository的findByStudentId）
            ClubMember currentMember = clubMemberRepository.findByStudentId(currentUser.getId());
            if (currentMember == null) {
                throw new CustomerException("权限不足，仅社团成员可查看本社团成员列表");
            }
            // 限制只能查询自己所在的社团
            clubMember.setClubId(currentMember.getClubId());
            example = Example.of(clubMember);
        }
        // 3. 其他未定义角色：无权限
        else {
            throw new CustomerException("权限不足，无法查看成员列表");
        }

        // 调用Repository的分页查询（按条件+分页）
        return clubMemberRepository.findAll(example, pageable);
    }

    // 添加社团成员（调用Repository的insert）
    public void addMember(ClubMember clubMember) {
        // 新增校验：检查学生是否已加入其他社团（使用Repository的findByStudentId）
        ClubMember existingMembers = clubMemberRepository.findByStudentId(clubMember.getStudentId());
        if (existingMembers != null) {
            throw new CustomerException("一个学生只能加入一个社团，无法重复加入");
        }
        if (clubMember.getRole() == null) {
            clubMember.setRole("MEMBER");
        }
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
}
