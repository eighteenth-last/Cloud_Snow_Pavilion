package com.linlee.cloudsnow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LINLEE 柠檬茶 SaaS系统 - 主启动类
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@SpringBootApplication
@MapperScan("com.linlee.cloudsnow.**.mapper")
public class CloudSnowPavilionApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudSnowPavilionApplication.class, args);
        System.out.println("\n" +
                "  _      _____ _   _ _      ______ ______ \n" +
                " | |    |_   _| \\ | | |    |  ____|  ____|\n" +
                " | |      | | |  \\| | |    | |__  | |__   \n" +
                " | |      | | | . ` | |    |  __| |  __|  \n" +
                " | |____ _| |_| |\\  | |____| |____| |____ \n" +
                " |______|_____|_| \\_|______|______|______|\n" +
                "                                          \n" +
                " :: LINLEE 柠檬茶 SaaS系统启动成功 ::\n" +
                " :: 接口文档: http://localhost:8080/api/doc.html ::\n");
    }
}
