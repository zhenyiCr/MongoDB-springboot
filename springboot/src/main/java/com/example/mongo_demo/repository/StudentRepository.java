package com.example.mongo_demo.repository; // 通常MongoDB的Repository放在repository包下


import com.example.mongo_demo.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

// 继承MongoRepository，泛型为实体类(Student)和主键类型(通常为String，对应MongoDB的_id)
public interface StudentRepository extends MongoRepository<Student, String> {

    // 对应原selectAll：根据条件查询所有
    List<Student> findAll();

    // 对应原insert：插入单个文档（MongoDB中insert通常返回插入的实体）
    Student insert(Student student);

    // 对应原selectByUsername：根据用户名查询
    // 可省略@Query，Spring Data会根据方法名自动生成查询条件（{'username': ?0}）
    Student findByUsername(String username);


    // 对应原selectUsername：根据ID查询用户名（修正参数：原方法实际用id查询，参数应为String id）
    @Query(value = "{'_id': ?0}", fields = "{'username': 1, '_id': 0}") // 只返回username字段（1表示返回，0表示不返回）
    String findUsernameById(String id);

    // 对应原deleteById：根据ID删除（MongoRepository默认有deleteById方法，也可显式定义）
    @Query(value = "{'_id': ?0}")
    void deleteById(String id);

    // 对应原selectById：根据ID查询
    // 可省略@Query，Spring Data会自动解析为{'_id': ?0}
    Student findStudentById(String id);
}