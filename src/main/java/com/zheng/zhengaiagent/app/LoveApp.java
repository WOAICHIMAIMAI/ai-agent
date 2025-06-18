package com.zheng.zhengaiagent.app;

import com.zheng.zhengaiagent.advisor.MyLoggerAdvisor;
import com.zheng.zhengaiagent.advisor.MySensitiveWordAdvisor;
import com.zheng.zhengaiagent.chatmemory.DatabaseChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            【角色设定】
            你是一位深耕恋爱心理领域的智能顾问，同时具备专业的伴侣匹配推荐能力。你能为用户提供两种服务：
                        
            恋爱心理咨询：解决单身/恋爱/已婚人群的情感问题
                        
            理想伴侣推荐：根据用户需求匹配潜在合适对象
                        
            【核心功能】
            一、心理咨询模式
            当用户倾诉情感问题时，按以下流程处理：
                        
            状态识别：通过提问确认用户当前情感状态（单身/恋爱/已婚）
                        
            问题诊断：
                        
            单身状态：询问社交圈拓展及追求心仪对象的困扰
                   
            恋爱状态：挖掘沟通、习惯差异引发的矛盾
                        
            已婚状态：探讨家庭责任与亲属关系处理问题
                        
            解决方案：引导用户详述事情经过、对方反应及自身想法后，给出专业建议
                        
            二、新增伴侣推荐模式
            当用户表达寻找伴侣需求时：
                        
            启动条件：用户主动询问"推荐对象"或"找伴侣"等关键词时触发
                                         
            智能匹配：
                                          
            展示3-5位匹配者（格式：昵称+年龄+匹配亮点）               
                        
            【交互规则】
                        
            敏感词处理：当检测到敏感词时立即终止当前对话，回复："您的对话中包含敏感词"
                        
            模式切换：两种服务模式可自然过渡，优先处理心理求助需求
                        
            隐私声明：告知用户所有推荐数据均为模拟生成，实际交友需谨慎
                        
            【开场白】
            "您好，我是您的恋爱顾问Alex，可以为您提供：
            ① 情感问题诊断（单身/恋爱/已婚阶段的烦恼）
            ② 理想伴侣匹配服务
            您今天需要哪方面的帮助呢？"
            """;

    /**
     * ChatClient初始化
     * @param dashscopeChatModel
     */
    public LoveApp(ChatModel dashscopeChatModel, DatabaseChatMemory databaseChatMemory){
        // 初始化基于文件的记忆
        String fileDir = System.getProperty("user.dir") + "/temp/chat-memory";
//        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // 初始化基于内存的记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(databaseChatMemory),
                        // 自定义日志Advisor，可按需开启
                        new MyLoggerAdvisor(),
                        // 自定义敏感词检测Advisor
                        new MySensitiveWordAdvisor()
                        // 自定义推理增强Advisor，可按需开启
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }

    record LoveReport(String title, List<String> suggestions){}

    /**
     * AI 俩年报告功能（实战结构化输出）
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId){
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("LoveReport: {}", loveReport);
        return loveReport;
    }

    // 支持 RAG 功能的 AI 多轮对话
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    /**
     * 和 RAG 知识库进行对话
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRAG(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                // 引用 RAG 知识库问答
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                // 应用 RAG 检索增强服务 （基于云知识库服务）
                .advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        return content;
    }
}
