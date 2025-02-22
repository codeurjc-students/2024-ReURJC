package com.example.Asistance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AsistanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsistanceApplication.class, args);
	}

}
