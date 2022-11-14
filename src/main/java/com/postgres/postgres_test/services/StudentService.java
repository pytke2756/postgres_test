package com.postgres.postgres_test.services;

import com.postgres.postgres_test.models.Student;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class StudentService {
    private JSONObject obj;
    private final HttpHeaders responseHeaders;
    private String DATA = "{\"status\": \"%d\", \"description\": \"%s\"}";
    private String json;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StudentService(){
        responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
    }

    public ResponseEntity<String> get() {
        List<Student> studentList = jdbcTemplate.query("SELECT id, name FROM students",
                new RowMapper<Student>() {
                    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Student student = new Student();
                        student.setId(rs.getInt("id"));
                        student.setName(rs.getString("name"));
                        return student;
                    }
                });
        jsonStringGenerateWithHttpData(DATA,  HttpStatus.OK.value(), "Students were found!");
        JSONArray studentJsonArray = new JSONArray(studentList);
        obj.put("data", studentJsonArray);

        return ResponseEntity.ok().headers(responseHeaders).body(obj.toString());
    }

    public ResponseEntity<String> get(Integer id) {
        Student student;
        try {
            student = jdbcTemplate.queryForObject("SELECT id, name FROM students WHERE id = ?",new RowMapper<Student>() {
                public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    return student;
                }
            }, id);

            jsonStringGenerateWithHttpData(DATA, HttpStatus.OK.value(), "Student was found!");
            JSONObject studentObject = new JSONObject(student);
            obj.put("data", studentObject);
            return ResponseEntity.ok().headers(responseHeaders).body(obj.toString());

        }catch (EmptyResultDataAccessException emptyResultDataAccessException){
            jsonStringGenerateWithHttpData(DATA, HttpStatus.NOT_FOUND.value(), "Student was not found!");
            return ResponseEntity.badRequest().headers(responseHeaders).body(obj.toString());
        }
    }

    public ResponseEntity<String> delete(Integer id) {
        int deletedRows = jdbcTemplate.update("DELETE FROM students WHERE id = ?", id);

        if (deletedRows > 0){
            jsonStringGenerateWithHttpData(DATA, HttpStatus.OK.value(), "Delete was successfully!");
            //return new ResponseEntity<>("Delete was successfully!", HttpStatus.ACCEPTED);
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(obj.toString());
        }
        else{
            jsonStringGenerateWithHttpData(DATA, HttpStatus.BAD_REQUEST.value(), "Delete was not successfully!");
            //the json data other order when use JSONOBJECT

            return ResponseEntity.badRequest()
                    .headers(responseHeaders)
                    .body(obj.toString());

        }
    }

    public ResponseEntity<String> post(Student student) {
        responseHeaders.set("Accept", "application/json");

        try{
            int update = jdbcTemplate.update("INSERT INTO students (name) VALUES (?)", student.getName());
            if(update > 0){
                json = String.format(DATA, HttpStatus.CREATED.value(), "Post Successfully!");
                obj = new JSONObject(json);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .headers(responseHeaders)
                        .body(obj.toString());
            }
        }catch (DataAccessException e){
            json = String.format(DATA, HttpStatus.FORBIDDEN.value(), "Post Unsuccessfully!");
            obj = new JSONObject(json);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .headers(responseHeaders)
                    .body(obj.toString());
        }
        return null;
    }

    private void jsonStringGenerateWithHttpData(String data, int httpStatus, String description){
        json = String.format(data, httpStatus, description);
        obj = new JSONObject(json);
    }

}
