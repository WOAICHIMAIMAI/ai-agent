package com.zheng.zhengaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zheng.zhengaiagent.mapper")
@SpringBootApplication
public class ZhengAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhengAiAgentApplication.class, args);
    }

}
