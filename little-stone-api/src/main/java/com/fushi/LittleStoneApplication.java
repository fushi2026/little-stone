package com.fushi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.fushi.mapper")
public class LittleStoneApplication {
	public static void main(String[] args) {
		SpringApplication.run(LittleStoneApplication.class, args);
	}
}
