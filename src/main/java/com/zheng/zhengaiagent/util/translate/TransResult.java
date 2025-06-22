package com.zheng.zhengaiagent.util.translate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * 百度翻译单个结果的封装
 */
public class TransResult {

    private String src;

    private String dst;
}
