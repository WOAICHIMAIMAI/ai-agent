package com.zheng.zhengaiagent.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 敏感词工具类
 */
public class TrieUtils {

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


    //通过map保存子树
    private Map<Character, TrieUtils> children;
    private boolean isEnd;

    public TrieUtils() {
        children = new HashMap<>();
        isEnd = false;
    }

    //将敏感词插入到前缀树中
    public void insert(String word) {
        TrieUtils node = this;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieUtils childrenNode = node.children.get(ch);
            if (childrenNode == null) {
                node.children.put(ch, new TrieUtils());
            }
            node = node.children.get(ch);
        }
        node.isEnd = true;
    }

    // 检测文本中是否包含敏感词，并返回第一个匹配到的敏感词
    public String detectSensitiveWord(String word, int start) {
        TrieUtils node = this;
        StringBuilder detectedWord = new StringBuilder();

        for (int i = start; i < word.length(); i++) {
            char ch = word.charAt(i);
            TrieUtils childrenNode = node.children.get(ch);

            if (childrenNode == null) {
                // 说明后面没有匹配的了
                break;
            } else {
                detectedWord.append(ch);
                node = childrenNode;
                if (node.isEnd) {
                    return detectedWord.toString(); // 返回检测到的敏感词
                }
            }
        }

        return null; // 没有匹配到敏感词
    }

    //检查一个单词是否存在敏感词
    public String sensitiveWord(String word) {
        //不断向前移动检查
        for (int i = 0; i < word.length(); i++) {
            if (!Objects.isNull(detectSensitiveWord(word, i))) {
                return detectSensitiveWord(word, i);
            }
        }
        return null;
    }

}