package com.example.mongo_demo.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.mongo_demo.entity.Notice;
import com.example.mongo_demo.exception.CustomerException;
import com.example.mongo_demo.repository.NoticeRepository;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {
    @Resource
    NoticeRepository noticeRepository;


    public List<Notice> selectAll(Notice notice) {
        // 构建动态查询条件（非null字段作为查询条件）
        Example<Notice> example = Example.of(notice);
        return noticeRepository.findAll(example);
    }

    public void add(Notice notice) {
        // 判断标题是否已存在（调用Repository的findByTitle）
        Notice dbNotice = noticeRepository.findByTitle(notice.getTitle());
        if (dbNotice != null) {
            throw new CustomerException("标题已存在");
        }
        if (StrUtil.isBlank(notice.getContent())) {
            notice.setContent("无");
        }
        notice.setTime(DateUtil.now());
        noticeRepository.insert(notice);
    }


    public Page<Notice> selectPage(Integer pageNum, Integer pageSize, Notice notice) {
        // MongoDB分页页码从0开始，需减1
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        // 构建查询条件
        Example<Notice> example = Example.of(notice);
        // 调用Repository的分页查询
        return noticeRepository.findAll(example, pageable);
    }


    public void update(Notice notice) {
        // 先查询原标题（调用Repository的findTitleById）
        String originalTitle = noticeRepository.findTitleById(notice.getId());
        // 若标题有修改，检查新标题是否已存在
        if (!originalTitle.equals(notice.getTitle())) {
            Notice dbNotice = noticeRepository.findByTitle(notice.getTitle());
            if (dbNotice != null) {
                throw new CustomerException("标题已存在");
            }
        }
        notice.setTime(DateUtil.now());
        noticeRepository.save(notice);
    }

    public void deleteById(String id) {
        noticeRepository.deleteById(id);
    }

    public void deleteBatch(List<Notice> list) {
        for (Notice notice : list) {
            this.deleteById(notice.getId());
        }
    }
}
