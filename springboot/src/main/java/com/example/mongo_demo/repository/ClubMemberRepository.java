package com.example.mongo_demo.repository;

import com.example.mongo_demo.entity.ClubMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface ClubMemberRepository extends MongoRepository<ClubMember, String> {

    // 对应原selectByClubId：根据社团ID查询成员
    List<ClubMember> findByClubId(String clubId);

    // 对应原insert：添加社团成员（MongoDB中insert通常返回插入的实体）
    ClubMember insert(ClubMember clubMember);

    // 对应原deleteById：移除社团成员（MongoRepository默认提供deleteById，可显式保留）
    void deleteById(String id);

    // 自定义更新：根据ID更新role字段
    @Query("{'_id': ?0}") // 条件：匹配ID为第一个参数的记录
    @Update("{'$set': {'role': ?1}}") // 更新逻辑：设置role为第二个参数
    void updateRoleById(String id, String newRole);

    // 对应原selectAll：查询所有社团成员（按条件过滤）
    List<ClubMember> findAll();

    // 对应原selectById：根据ID查询社团成员
    ClubMember findClubMembersById(String id);

    // 对应原selectByStudentId：根据学生ID查询成员（limit 1对应返回单个实体）
    ClubMember findByStudentId(String studentId);

    // 对应原selectByStudentIdAndClubId：根据学生ID和社团ID查询成员
    ClubMember findByStudentIdAndClubId(String studentId, String clubId);

    // 对应原selectLeaderByStudentId：根据学生ID查询其作为"领导者"的记录
    ClubMember findByStudentIdAndRole(String studentId, String role);
}
