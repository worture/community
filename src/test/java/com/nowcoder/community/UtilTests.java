package com.nowcoder.community;

import com.nowcoder.community.pojo.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UtilTests {
    @Autowired
    private UserService userService;


    @Test
    public void testMD5() {
        User user = userService.getUserById(156);
        String string1 = "11111111";
        String string2 = "zzzzzzzz";
        String password = user.getPassword();
        String s = CommunityUtil.md5(string2 + user.getSalt());
        System.out.println(s);
        System.out.println(password);
    }
}
