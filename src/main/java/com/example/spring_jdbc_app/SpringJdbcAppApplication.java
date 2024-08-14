package com.example.spring_jdbc_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class SpringJdbcAppApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(SpringJdbcAppApplication.class);

	@Autowired
	JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringJdbcAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Creating DB Tables");

		jdbcTemplate.execute("DROP TABLE CUSTOMERS IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE CUSTOMERS(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> customersNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" ")).collect(Collectors.toList());

		customersNames
				.forEach(name -> logger.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		jdbcTemplate.batchUpdate("INSERT INTO CUSTOMERS(first_name, last_name) VALUES(?, ?)", customersNames);

		logger.info("Querying for customer records where first_name = 'Josh':");

		jdbcTemplate.query("SELECT id, first_name, last_name FROM CUSTOMERS WHERE first_name= ?",
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")),
				"Josh").forEach(customer -> logger.info(customer.toString()));
	}
}
