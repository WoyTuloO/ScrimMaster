package com.woytuloo.ScrimMaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScrimMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrimMasterApplication.class, args);
	}

}
