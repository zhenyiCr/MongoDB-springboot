package com.example.mongo_demo.repository;

import com.example.mongo_demo.entity.Notice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NoticeRepository extends MongoRepository<Notice, String> {

    // 对应原selectAll：根据条件查询所有通知
    // 通过传入的Notice对象构建动态查询条件（非null字段作为查询条件）
    List<Notice> findAll();

    // 对应原insert：新增通知（MongoDB中insert返回插入的实体，包含自动生成的_id）
    Notice insert(Notice notice);

    // 对应原selectByTitle：根据标题查询通知
    // Spring Data会自动解析为：{'title': ?0}
    Notice findByTitle(String title);

    // 对应原selectTitle：根据ID查询通知标题（修正参数：原逻辑用id查询，参数应为String id）
    @Query(value = "{'_id': ?0}", fields = "{'title': 1, '_id': 0}") // 只返回title字段（排除_id）
    String findTitleById(String id);

    // 对应原deleteById：根据ID删除通知（MongoRepository默认提供该方法，显式保留便于理解）
    void deleteById(String id);
}
