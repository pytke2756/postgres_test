package com.postgres.postgres_test.controllers;

import com.postgres.postgres_test.models.Student;
import com.postgres.postgres_test.services.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/students")
    public ResponseEntity<String> getStudents(){

        return studentService.get();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<String> getSpecificStudent(@PathVariable(value = "id") Integer id){
        return studentService.get(id);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<String> deleteSpecificStudent(@PathVariable(value = "id") Integer id){
        return studentService.delete(id);
    }

    @PostMapping("/students")
    public ResponseEntity<String> addStudent(@RequestBody Student student){
        return studentService.post(student);
    }
}
