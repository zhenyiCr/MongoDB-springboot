package com.example.mongo_demo.controller;


import com.example.mongo_demo.common.Result;
import com.example.mongo_demo.entity.Account;
import com.example.mongo_demo.entity.Club;
import com.example.mongo_demo.service.ClubService;
import com.example.mongo_demo.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/club")
public class ClubController {

    @Resource
    ClubService clubService;


    @PostMapping("/add")
    public Result add(@RequestBody Club club) { // @RequestBody 接受前端传来的json数据
        clubService.add(club);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable String id) { // @PathVariable 接受路径参数
        clubService.deleteById(id);
        return Result.success();
    }
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Club> list) {
        clubService.deleteBatch(list);
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody Club club) {
        clubService.update(club);
        return Result.success();
    }

    @GetMapping("/selectAll")
    public Result selectAll(Club club) {
        List<Club> clubList = clubService.selectAll(club);
        return Result.success(clubList);
    }

    // 分页查询
    // pageNum 当前页数
    // pageSize 每页显示的条数
    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                            @RequestParam(defaultValue = "10") Integer pageSize,
                            Club club) {
        Page<Club> pageInfo = clubService.selectPage(pageNum,pageSize,club);
        return Result.success(pageInfo);
    }


}
