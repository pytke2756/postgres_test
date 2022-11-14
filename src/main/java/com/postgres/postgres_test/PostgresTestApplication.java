package com.postgres.postgres_test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class PostgresTestApplication implements CommandLineRunner {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(PostgresTestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		jdbcTemplate.execute("DROP TABLE IF EXISTS students");
		String sqlCreateTable = "CREATE TABLE students(id SERIAL PRIMARY KEY, name VARCHAR(50) UNIQUE NOT NULL)";

		jdbcTemplate.execute(sqlCreateTable);

		String sqlInsert = "INSERT INTO students (name) VALUES ('Buzi Gyerek')";
		int rows = jdbcTemplate.update(sqlInsert);
		if(rows > 0){
			System.out.println("A new row has been inserted.");
		}
		 jdbcTemplate.execute("INSERT INTO students (name) VALUES ('Fasszopó Gyerek')");

	}
}
