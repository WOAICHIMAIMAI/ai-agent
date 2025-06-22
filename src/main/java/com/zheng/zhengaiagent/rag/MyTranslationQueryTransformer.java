package com.zheng.zhengaiagent.rag;

import com.zheng.zhengaiagent.util.translate.LanguageTranslateByBaiDu;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyTranslationQueryTransformer{

    @Resource
    private LanguageTranslateByBaiDu languageTranslateByBaiDu;

    public String transform(Query query) {
        String queryText = query.text();
        String translateText = languageTranslateByBaiDu.autoTranslate(queryText, "zh");
        return translateText;
    }

}
