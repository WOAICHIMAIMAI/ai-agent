package com.zheng.zhengaiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDateTime;

/**
 * 获取当前时间
 */
public class NowTimeTool {
    @Tool(description = "acquire current time")
    public String nowTime(){
        return "Current time is " + LocalDateTime.now();
    }
}
