package com.example.crud.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crud.model.Student;
import com.example.crud.repository.StudentRepository;

@Service // spirng이 service bean으로 인식하게 함, 비지니스로직을 담당하는 곳, 컴트롤러와 리포지토리 사이 중간다리
public class StudentService {

    @Autowired
    private StudentRepository studentRepository; // StudentRepository를 사용하여 Student 데이터에 대한 작업 처리



    public List<Student> getAllStudents() {     // findAll을 호출해 모든 Student entity를 조회하여 리스트로 반환
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {    // 특정 id 값을 가진 Student entity를 조회, findById(id) 메서드를 사용 값이 없을 경우 null값 반환
        return studentRepository.findById(id).orElse(null);
    }

    public Student saveStudent(Student student) { // 새로운 학생정보가 포함된 student 객체를 저장하거나 업데이트함.
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {        // 주어진 id 값을 가진 student 엔티티 삭제
        studentRepository.deleteById(id);
    }
}