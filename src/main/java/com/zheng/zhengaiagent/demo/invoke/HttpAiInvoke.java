package com.zheng.zhengaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;

public class HttpAiInvoke {
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String API_KEY = TestApiKey.API_KEY; // 替换成您的API密钥

    public static void main(String[] args) {
        // 构建消息数组
        JSONArray messages = new JSONArray();
        
        // 添加system消息
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");
        messages.add(systemMessage);
        
        // 添加user消息
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");
        messages.add(userMessage);
        
        // 构建input对象
        JSONObject input = new JSONObject();
        input.set("messages", messages);
        
        // 构建parameters对象
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        
        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");
        requestBody.set("input", input);
        requestBody.set("parameters", parameters);
        
        try {
            // 发送POST请求
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .execute();
            
            // 获取响应结果
            String result = response.body();
            System.out.println("API响应结果：");
            System.out.println(result);
            
        } catch (Exception e) {
            System.err.println("调用API出错：" + e.getMessage());
            e.printStackTrace();
        }
    }
}