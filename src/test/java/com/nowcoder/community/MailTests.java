package com.nowcoder.community;
import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.xml.transform.Templates;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)

public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail() {
        mailClient.sendMail("worture@163.com", "TEST", "Hello!Is anybody in there?");
    }

    @Test
    public void testHTMLMail() {
        Context context = new Context();
        context.setVariable("username", "Worture");

        String content = templateEngine.process("mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("worture@163.com", "HTML", content);
    }
}
