package com.example.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.crud.model.Student;

// JpaRepository 를 상속하여 Student entity 에 대한 CRUD 기능을 자동 제공
public interface StudentRepository extends JpaRepository<Student, Long>{
    
}
