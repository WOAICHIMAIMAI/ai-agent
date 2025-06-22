package com.zheng.zhengaiagent.util.translate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 百度翻译返回结果
 */
public class TranslateResult {

    private String from;

    private String to;

    private List<TransResult> transResult;

    private Integer errorCode;

    private String errorMsg;

}
