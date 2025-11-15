package com.example.mongo_demo.service;

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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ClubService {
    @Resource
    ClubRepository clubRepository;
    @Resource
    ClubMemberRepository clubMemberRepository;

    @Resource
    private StudentRepository studentRepository;


    // 新增：查询社团时根据角色过滤
    public List<Club> selectAll(Club club) {
        Example<Club> example = Example.of(club);
        List<Club> clubs = clubRepository.findAll(example);

        // 关联查询社长姓名
        if (!clubs.isEmpty()) {
            // 1. 收集所有社团的leaderId（去重，过滤null）
            Set<String> leaderIds = clubs.stream()
                    .map(Club::getLeaderId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (!leaderIds.isEmpty()) {
                // 2. 批量查询社长对应的学生信息（id -> Student映射）
                Map<String, Student> leaderMap = studentRepository.findAllById(leaderIds).stream()
                        .collect(Collectors.toMap(Student::getId, Function.identity()));

                // 3. 为每个社团设置社长姓名
                for (Club c : clubs) {
                    Student leader = leaderMap.get(c.getLeaderId());
                    if (leader != null) {
                        c.setLeaderName(leader.getName()); // 赋值社长姓名
                    }
                }
            }
        }
        return clubs;
    }

    // 校验社团操作权限（ADMIN无限制，LEADER只能操作自己的社团）
    private void checkClubPermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 管理员拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色需要校验是否为该社团的LEADER
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前用户在该社团的角色（调用Repository的findByStudentId）
            ClubMember currentMember = clubMemberRepository.findByStudentIdAndClubId(
                    currentUser.getId(), clubId
            );
            if (currentMember == null
                    || !"LEADER".equals(currentMember.getRole())
                    || !currentMember.getClubId().equals(clubId)) {
                throw new CustomerException("无权限操作该社团");
            }
        } else {
            throw new CustomerException("权限不足，无法操作社团");
        }
    }


    public void add(Club club) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (!"ADMIN".equals(currentUser.getRole()) && !"MANAGER".equals(currentUser.getRole())) {
            throw new CustomerException("无权限创建社团");
        }
        // 判断社团名称是否已存在（调用Repository的findByName）
        Club dbClub = clubRepository.findByName(club.getName());
        if (dbClub != null) {
            throw new CustomerException("名称已存在");
        }
        club.setStatus("ACTIVE");
        clubRepository.insert(club);
    }


    public Page<Club> selectPage(Integer pageNum, Integer pageSize, Club club) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Example<Club> example = Example.of(club);
        Page<Club> clubPage = clubRepository.findAll(example, pageable);
        List<Club> clubs = clubPage.getContent();

        // 关联查询社长姓名（逻辑同selectAll）
        if (!clubs.isEmpty()) {
            Set<String> leaderIds = clubs.stream()
                    .map(Club::getLeaderId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (!leaderIds.isEmpty()) {
                Map<String, Student> leaderMap = studentRepository.findAllById(leaderIds).stream()
                        .collect(Collectors.toMap(Student::getId, Function.identity()));

                for (Club c : clubs) {
                    Student leader = leaderMap.get(c.getLeaderId());
                    if (leader != null) {
                        c.setLeaderName(leader.getName());
                    }
                }
            }
        }

        // 封装处理后的分页结果
        return new PageImpl<>(clubs, pageable, clubPage.getTotalElements());
    }


    public void update(Club club) {
        // 1. 校验权限（原逻辑保留）
        checkClubPermission(club.getId());

        // 2. 校验待更新的社团是否存在
        Optional<Club> existingClubOpt = clubRepository.findById(club.getId());
        if (existingClubOpt.isEmpty()) {
            throw new CustomerException("该社团不存在，无法更新");
        }
        Club existingClub = existingClubOpt.get(); // 存在的社团实体

        // 3. 校验名称是否重复（基于存在的社团获取原始名称）
        String originalName = existingClub.getName();
        if (!originalName.equals(club.getName())) { // 名称有修改
            Club dbClub = clubRepository.findByName(club.getName());
            if (dbClub != null) {
                throw new CustomerException("名称已存在");
            }
        }

        // 4. 执行更新（使用存在的社团实体确保必要字段不丢失，可选）
        // 若前端可能未传递所有字段，建议合并现有数据与更新数据
        existingClub.setName(club.getName());
        existingClub.setDescription(club.getDescription());
        existingClub.setStatus(club.getStatus());
        existingClub.setLeaderId(club.getLeaderId());
        // ... 其他需要更新的字段

        clubRepository.save(existingClub); // 保存更新后的实体
    }

    public void deleteById(String id) {
        if (!TokenUtils.getCurrentUser().getRole().equals("ADMIN")) {
            throw new CustomerException("只有管理员可以删除社团");
        }
        clubRepository.deleteById(id);
    }



    public void deleteBatch(List<Club> list) {
        if (!TokenUtils.getCurrentUser().getRole().equals("ADMIN")) {
            throw new CustomerException("只有管理员可以删除社团");
        }
        for (Club club : list) {
            this.deleteById(club.getId());
        }
    }

}
