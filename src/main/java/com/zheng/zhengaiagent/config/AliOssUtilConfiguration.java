package com.zheng.zhengaiagent.config;

import com.zheng.zhengaiagent.properties.AliOssProperties;
import com.zheng.zhengaiagent.util.AliOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliOssUtilConfiguration {

    @Bean
    public AliOSSUtil aliOSSUtil(AliOssProperties aliOssProperties){
        log.info("开始创建阿里云文件上传工具类对象:{}", aliOssProperties);
        return new AliOSSUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
