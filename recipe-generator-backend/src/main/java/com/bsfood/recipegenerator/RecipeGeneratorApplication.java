package com.bsfood.recipegenerator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bsfood.recipegenerator.mapper")
public class RecipeGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(RecipeGeneratorApplication.class, args);
    }
}