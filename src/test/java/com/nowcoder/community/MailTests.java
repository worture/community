package com.nowcoder.community;
import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)

public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Test
    public void testMail() {
        mailClient.sendMail("worture@163.com", "TEST", "Hello!Is anybody in there?");
    }
}