package com.example.todoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class }) //"Don't use your default configs, use my configs"
public class TodoapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoapiApplication.class, args);
	}

}
