package com.zheng.zhengaiagent.util;

import com.zheng.zhengaiagent.util.translate.LanguageTranslateByBaiDu;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LanguageTranslateByBaiDuTest {

    @Resource
    private LanguageTranslateByBaiDu languageTranslateByBaiDu;

    @Test
    void autoTranslate() {
        String q = "I am people";
        String text = languageTranslateByBaiDu.autoTranslate(q, "zh");
        System.out.println(text);
        Assertions.assertNotNull(text);
    }

    @Test
    void translate() {
    }
}