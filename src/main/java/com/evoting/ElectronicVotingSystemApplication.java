package com.evoting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ElectronicVotingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicVotingSystemApplication.class, args);
	}

}