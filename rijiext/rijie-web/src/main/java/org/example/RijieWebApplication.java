package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.mapper") // 扫描org.example.mapper包下的所有Mapper接口
public class RijieWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(RijieWebApplication.class, args);
    }

}
