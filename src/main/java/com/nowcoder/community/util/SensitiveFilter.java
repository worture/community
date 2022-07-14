package com.nowcoder.community.util;

import com.sun.scenario.animation.shared.TimerReceiver;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.text.normalizer.Trie;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);

    //替换词
    private static final String REPLACEMENT = "***";

    //初始化根节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));

        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null){
                //添加到前缀树
                this.addKeyWord(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败： " + e.getMessage());
        }


    }

    //添加敏感词到前缀树
    private void addKeyWord(String keyword) {
        TrieNode node = root;
        for (char ch : keyword.toCharArray()) {
            TrieNode subNode = node.getSubNode(ch);

            if (subNode == null) {
                //初始化子节点
                subNode = new TrieNode();
                node.addSubNode(ch, subNode);
            }

            //指针往下移动
            node = subNode;

            //设置标记
            if (ch == keyword.charAt(keyword.length()-1)) {
                node.setWord(true);
            }
        }

    }

    /**
     *
     * @param text 原文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1 遍历Trie
        TrieNode tmpNode = root;

        //指针2 指针3 双指针分割字符串
        int begin = 0;

        int end = 0;

        StringBuilder sb = new StringBuilder();

        int len = text.length();
        //遍历每一个字符位置作为子串开头字符
        while (begin < len) {
            char startChar = text.charAt(begin);
            TrieNode node = root.getSubNode(startChar);
            //开始的字符不在根节点的子树中
            if (node == null) {
                sb.append(startChar);
            } else {
                boolean haveReplaced = false;
                end++;
                //开始的字符在根节点的子树中，疑似敏感词，移动end开始匹配
                TrieNode tmp = node;
                while (end < len) {
                    char endChar = text.charAt(end);
                    //跳过特殊字符
                    while (end < len && isSymbol(text.charAt(end))) {
                        end++;
                    }
                    endChar = text.charAt(end);
                    //前缀树的指针向下移动
                    tmp = tmp.getSubNode(endChar);
                    //后序匹配成功
                    if (tmp != null && tmp.isWord()) {
                        //替换后两个指针移动到下一个位置并跳出
                        sb.append(REPLACEMENT);
                        haveReplaced = true;
                        begin = end++;
                        break;
                    }
                    //暂时未匹配到
                    else if (tmp != null && !tmp.isWord()) {
                        end++;
                    } else {
                        //非敏感词,将startChar字符添加上，begin向后移动
                        sb.append(startChar);
                        break;
                    }
                }
                //可能text是敏感词的前缀end超出范围也将startChar添加上
                if (end == len && !haveReplaced) {
                    sb.append(startChar);
                }
            }
            end = ++begin;
        }

        return sb.toString();
    }

    //判断特殊符号
    private boolean isSymbol(Character c) {
        //0x2E80~0x9FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //前缀树
    private class TrieNode {

        //是否以该字符结束
        private boolean isWord = false;

        //子节点
        private Map<Character, TrieNode> subNode= new HashMap<>();

        public boolean isWord() {
            return isWord;
        }

        public void setWord(boolean word) {
            isWord = word;
        }

        //添加子节点
        public void addSubNode(Character ch, TrieNode node) {
            subNode.put(ch, node);
        }

        //获取子节点
        public TrieNode getSubNode(Character ch) {
            return subNode.get(ch);
        }
    }

}
