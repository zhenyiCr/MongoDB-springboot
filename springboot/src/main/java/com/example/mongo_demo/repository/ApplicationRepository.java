package com.example.mongo_demo.repository;

import com.example.mongo_demo.entity.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends MongoRepository<Application, String> {

    // 对应原selectAll：根据条件查询所有申请
    // 通过传入的Application对象构建动态查询条件（非null字段作为查询条件）
    List<Application> findAll();

    // 对应原insert：新增申请（MongoDB中insert返回插入的实体，包含自动生成的_id）
    Application insert(Application application);

    // 对应原selectById：根据ID查询申请（返回Optional避免空指针）
    Optional<Application> findById(String id);

    // 对应原deleteById：根据ID删除申请（MongoRepository默认提供，显式保留便于理解）
    void deleteById(String id);

    @Query("{'_id': ?0}") // 条件：匹配ID为第一个参数的记录
    @Update("{'$set': {'status': ?1}}") // 更新逻辑：设置status为第二个参数
    void updateStatusById(String id, String newStatus); // 假设status是String类型，根据实际类型调整

    // 对应原selectByStudentAndClub：根据学生ID和社团ID查询申请
    // 修正参数名（符合Java驼峰命名规范：StudentId→studentId，ClubId→clubId）
    // Spring Data自动解析为：{'studentId': ?0, 'clubId': ?1}
    Application findByStudentIdAndClubId(String studentId, String clubId);

}
