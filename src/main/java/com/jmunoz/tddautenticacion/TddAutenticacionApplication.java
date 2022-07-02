package com.jmunoz.tddautenticacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class TddAutenticacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(TddAutenticacionApplication.class, args);
	}

}
