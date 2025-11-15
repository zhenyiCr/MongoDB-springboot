package com.example.mongo_demo.repository;

import com.example.mongo_demo.entity.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends MongoRepository<Club, String> {

    // 对应原selectAll：根据条件查询所有社团
    // 通过Example自动构建查询条件（支持动态字段匹配）
    List<Club> findAll();

    // 对应原insert：新增社团（MongoDB中insert返回插入的实体，包含自动生成的_id）
    Club insert(Club club);

    // 对应原selectByName：根据名称查询社团（修正参数名：原注解用name字段，参数应与字段名一致）
    // Spring Data会自动解析为：{'name': ?0}
    Club findByName(String name);

    // 对应原selectById：根据ID查询社团（MongoDB中findById返回Optional，避免空指针）
    Optional<Club> findById(String id);


    // 对应原selectName：根据ID查询社团名称（修正参数：原逻辑用id查询，参数应为String id）
    @Query(value = "{'_id': ?0}", fields = "{'name': 1, '_id': 0}") // 只返回name字段（排除_id）
    String findNameById(String id);

    // 对应原deleteById：根据ID删除社团（MongoRepository默认提供该方法，显式保留便于理解）
    void deleteById(String id);
}
