package com.sporty.jackpot;

import org.springframework.boot.SpringApplication;

public class TestJackpotApplication {

	public static void main(String[] args) {
		SpringApplication.from(JackpotApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
