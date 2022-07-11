package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveWordTests {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveWord() {
//        String word = "啊啊博彩 啊啊嫖娼啊啊吸毒可开发票bin赌博博彩";
//        String filter = sensitiveFilter.filter(word);
//        System.out.println(filter);

        String word1 = "啊啊博※彩 啊啊嫖⭐娼啊啊吸毒可开🌙发票bin赌博博彩fabc";
        String filter1 = sensitiveFilter.filter(word1);
        System.out.println(filter1);
    }

}
