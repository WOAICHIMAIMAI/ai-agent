package com.zheng.zhengaiagent.advisor;

import com.zheng.zhengaiagent.util.TrieUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@Slf4j
public class MySensitiveWordAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    public static final String[] WORDS = {
            "赌博", "毒品", "色情", "诈骗", "暴力",
            "恐怖", "反动", "邪教", "自杀", "仇恨",
            "裸聊", "招嫖", "枪支", "弹药", "迷药",
            "假币", "代考", "黑客", "病毒", "木马",
            "分裂", "颠覆", "间谍", "窃密", "走私",
            "贩毒", "拐卖", "性侵", "猥亵", "强奸",
            "轮奸", "乱伦", "卖淫", "嫖娼", "包养",
            "代孕", "假证", "高利贷", "套路贷", "校园贷",
            "网络贷", "裸贷", "跑分", "洗钱", "黑产",
            "刷单", "钓鱼", "盗号", "撞库", "爆破",
            "肉鸡", "僵尸", "蠕虫", "后门", "勒索",
            "挖矿", "挂马", "渗透", "入侵", "窃听",
            "监听", "偷拍", "针孔", "破解", "翻墙",
            "VPN", "代理", "暗网", "黄网", "赌网",
            "毒网", "枪网", "假网", "骗网", "黑网",
            "灰产", "黑市", "暗市", "制毒", "吸毒",
            "贩枪", "制枪", "卖枪", "买枪", "军火",
            "炸药", "雷管", "导火", "炸弹", "爆燃",
            "纵火", "投毒", "劫持", "绑架", "撕票",
            "谋杀"
    };

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int getOrder() {
        return -10;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        String message = request.userText();
        log.info("敏感词检测中：{}", message);
        if(containsSensitiveWords(message)){
            return AdvisedRequest.from(request).userText("包含敏感词").build();
        }
        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
    }


    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }

    private boolean containsSensitiveWords(String message) {
        TrieUtils trie = new TrieUtils();
        for (String word : WORDS) {
            trie.insert(word);
        }

        return !Objects.isNull(trie.sensitiveWord(message));
    }
}
