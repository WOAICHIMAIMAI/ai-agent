package com.zheng.zhengaiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我叫郑嘉骏";
        String answer = loveApp.doChat(message, chatId);

        message = "我想让另一半（雪梨）更加爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        message = "我叫什么名字，你还记得吗？";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(message);

    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.fastUUID().toString();
        String message = "你好，我叫郑嘉骏，我想让我的另一半更爱我，但我不知道怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRAG() {
        String chatId = UUID.randomUUID().toString();
        String message = "I'm married, but I'm not very close after marriage, what should I do?";
        String answer = loveApp.doChatWithRAG(message, chatId);
        Assertions.assertNotNull(answer);
    }
}