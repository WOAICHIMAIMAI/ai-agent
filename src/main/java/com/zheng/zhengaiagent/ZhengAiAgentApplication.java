package com.zheng.zhengaiagent;

import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*@MapperScan("com.zheng.zhengaiagent.mapper")*/
@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class ZhengAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhengAiAgentApplication.class, args);
    }

}
