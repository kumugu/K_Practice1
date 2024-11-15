package com.example.crud.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.crud.model.Student;
import com.example.crud.service.StudentService;

@RestController
@RequestMapping("/api/students") // API 요청 경로로 /api/students로 설정
public class StudentRestController {
    
    @Autowired
    private StudentService studentService;

    // 전체 학생 목록 조회
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents(); // 모든 학생 정보 반환
    }

    // 특정 학생 조회
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id); // 특정 학생 정보 반환
    }

    // 새로운 학생 추가
    @PostMapping
    public Student creatStudent(@RequestBody Student student) {
        return studentService.saveStudent(student); // 새로운 학생 저장
    }

    // 기존 학생 정보 수정
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);  // URL 경로의 id 값을 설정
        return studentService.saveStudent(student); // 학생 정보 업데이트
    }

    // 특정 학생 삭제
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent((id));     // 학생 삭제
        return "Student with ID " + id + " has been deleted.";
    }
    

}