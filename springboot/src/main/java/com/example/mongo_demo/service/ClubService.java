package com.example.mongo_demo.service;

import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.Club;
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
import org.springframework.stereotype.Service;

@Service
public class ClubService {
    @Resource
    ClubRepository clubRepository;
    @Resource
    ClubMemberRepository clubMemberRepository;


    // 新增：查询社团时根据角色过滤
    public List<Club> selectAll(Club club) {
//        Account currentUser = TokenUtils.getCurrentUser();
//        // 学生只能查询自己的社团
//        if ("STUDENT".equals(currentUser.getRole())) {
//            // 查询当前学生是否属于某个社团（通过社团成员表关联）
//            ClubMember currentMember = clubMemberRepository.findByStudentId(currentUser.getId());
//            // 若不是任何社团的成员，无权限
//            if (currentMember == null) {
//                throw new CustomerException("权限不足，仅社团成员可查看自己所在的社团信息");
//            }
//            // 限制只能查询自己所在的社团（通过社团ID过滤）
//            club.setId(currentMember.getClubId()); // 强制查询当前学生所在的社团ID
//        }
        // 使用Example构建动态查询条件（非null字段作为查询条件）
        Example<Club> example = Example.of(club);
        return clubRepository.findAll(example);
    }

    // 新增：校验社团操作权限（ADMIN无限制，LEADER只能操作自己的社团）
    private void checkClubPermission(String clubId) {
        Account currentUser = TokenUtils.getCurrentUser();
        // 1. 管理员拥有所有权限
        if ("ADMIN".equals(currentUser.getRole())) {
            return;
        }
        // 2. 学生角色需要校验是否为该社团的LEADER
        if ("STUDENT".equals(currentUser.getRole())) {
            // 查询当前用户在该社团的角色（调用Repository的findByStudentId）
            ClubMember currentMember = clubMemberRepository.findByStudentId(currentUser.getId());
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
        checkClubPermission(club.getId());
        // 判断社团名称是否已存在（调用Repository的findByName）
        Club dbClub = clubRepository.findByName(club.getName());
        if (dbClub != null) {
            throw new CustomerException("名称已存在");
        }
        club.setStatus("ACTIVE");
        clubRepository.insert(club);
    }


    public Page<Club> selectPage(Integer pageNum, Integer pageSize, Club club) {
        // MongoDB分页页码从0开始，需减1
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 构建查询条件（非null字段作为过滤条件）
        Example<Club> example = Example.of(club);
        // 调用Repository的分页查询
        return clubRepository.findAll(example, pageable);
    }


    public void update(Club club) {
        checkClubPermission(club.getId());
        // 校验名称是否重复（先查询原名称）
        String originalName = clubRepository.findNameById(club.getId());
        // 若名称有修改，检查新名称是否已存在
        if (!originalName.equals(club.getName())) {
            Club dbClub = clubRepository.findByName(club.getName());
            if (dbClub != null) {
                throw new CustomerException("名称已存在");
            }
        }
        clubRepository.save(club);
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
