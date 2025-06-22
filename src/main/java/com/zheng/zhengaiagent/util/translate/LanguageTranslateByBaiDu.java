package com.zheng.zhengaiagent.util.translate;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用 百度翻译API 进行翻译
 */
@Component
@Slf4j
public class LanguageTranslateByBaiDu {
    @Value("${baidu.translate.app-id}")
    private String appid;
    @Value("${baidu.translate.secret-key}")
    private String secretKey;

    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    private final TimedCache<String, String> translationCache = CacheUtil.newTimedCache(60 * 60 * 1000);


    public String autoTranslate(String q, String to){
        return this.translate(q, "auto", to);
    }


    public String translate(String q, String from, String to){
        log.info("开始使用百度翻译API进行翻译：{}", q);
        if(translationCache.containsKey(q)){
            return translationCache.get(q);
        }
        Map<String, Object> requestMap = new HashMap<>();
        String salt = RandomUtil.randomNumbers(5);
        String sign = DigestUtil.md5Hex(appid + q + salt + secretKey);
        requestMap.put("q", q);
        requestMap.put("from", from);
        requestMap.put("to", to);
        requestMap.put("appid", appid);
        requestMap.put("salt", salt);
        requestMap.put("sign", sign);
        //链式构建请求
        String resultJson = HttpRequest.post(API_URL)
                .header(Header.CONTENT_TYPE, "application/x-www-form-urlencoded")//头信息，多个头信息多次调用此方法即可
                .form(requestMap)//表单内容
                .timeout(20000)//超时，毫秒
                .execute().body();
        TranslateResult result = JSONUtil.toBean(resultJson, TranslateResult.class);
        String dst = result.getTransResult().get(0).getDst();
        if(result.getErrorCode() != null){
            throw new RuntimeException("翻译API返回错误：" + result.getErrorMsg());
        }
        if(result.getTransResult() == null || result.getTransResult().isEmpty()){
            throw new RuntimeException("翻译API返回结果为空");
        }
        translationCache.put(q, dst);
        return dst;
    }
}
