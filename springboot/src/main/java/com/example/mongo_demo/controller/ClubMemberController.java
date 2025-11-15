package com.example.mongo_demo.controller;

import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.ClubMember;
import com.example.mongo_demo.service.ClubMemberService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubMember")
public class ClubMemberController {

    @Resource
    private ClubMemberService clubMemberService;

    // 根据社团ID查询成员
    @GetMapping("/getByClubId")
    public Result getByClubId(String clubId) {
        return Result.success(clubMemberService.getMembersByClubId(clubId));
    }

    // 分页查询
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             ClubMember clubMember) {
        Page<ClubMember> pageInfo = clubMemberService.selectPage(pageNum, pageSize, clubMember);
        return Result.success(pageInfo);
    }

    // 添加成员
    @PostMapping("/add")
    public Result add(@RequestBody ClubMember clubMember) {
        clubMemberService.addMember(clubMember);
        return Result.success();
    }

    // 移除成员
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) {
        clubMemberService.removeMember(id);
        return Result.success();
    }

    // 更新角色
    @PutMapping("/updateRole")
    public Result updateRole(@RequestBody ClubMember clubMember) {
        clubMemberService.updateMemberRole(clubMember);
        return Result.success();
    }
}