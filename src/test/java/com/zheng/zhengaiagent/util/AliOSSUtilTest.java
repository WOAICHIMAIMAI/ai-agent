package com.zheng.zhengaiagent.util;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AliOSSUtilTest {

    @Resource
    private AliOSSUtil aliOSSUtil;
    @Test
    void uploadFile() {
        String FILE_DIR = System.getProperty("user.dir") + "/temp/pdf";
        File file = new File("D:\\Java-development\\myProject\\zheng-ai-agent/temp/pdf/情人节约会计划.pdf");
        String s = aliOSSUtil.uploadFile(UUID.randomUUID()+ ".pdf", file);
        System.out.println(s);
    }

}