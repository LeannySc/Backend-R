package com.plasti_usos.reciclaje;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendReciclajeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendReciclajeApplication.class, args);
	}

}
