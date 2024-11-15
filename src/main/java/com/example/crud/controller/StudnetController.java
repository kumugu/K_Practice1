package com.example.crud.controller;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.crud.model.Student;
import com.example.crud.service.StudentService;
import java.util.List;

@RestController // JSON 데이터를 반환하는 RESTful 컨트롤러로 설정
@RequestMapping("/students")
public class StudnetController {

    @Autowired
    private StudentService studentService; // studentService를 호출해서 비지니스 로직 호출, 데이터처리
    
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents(); // 모든 학생 데이터를 JSON으로 반환
    }

    @PostMapping
    public Student savaStudent(@RequestBody Student student) {
        return studentService.saveStudent(student); // 저장된 학생 데이터를 JSON으로 반환
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);   // 특정 ID 학생 데이터 반환
    }

    @PutMapping("/{id}")
    public Student upStudent(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        return studentService.saveStudent(student); // 수정된 학생 데이터를 반환
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id); // 학생 삭제
    }
}
