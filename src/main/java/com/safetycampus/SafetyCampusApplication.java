package com.safetycampus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@MapperScan("com.safetycampus.**.mapper")
@SpringBootApplication
public class SafetyCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafetyCampusApplication.class, args);
    }

}
