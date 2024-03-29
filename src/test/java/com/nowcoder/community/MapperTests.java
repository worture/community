package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.pojo.DiscussPost;
import com.nowcoder.community.pojo.LoginTicket;
import com.nowcoder.community.pojo.Message;
import com.nowcoder.community.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectUser() {
        User user1 = userMapper.selectById(101);
        User user2 = userMapper.selectByName("zhangfei");
        User user3 = userMapper.selectByEmail("nowcoder146@sina.com");

        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("t");
        user.setPassword("11");
        user.setSalt("aaa");
        user.setEmail("ddd@q.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int i = userMapper.insertUser(user);
        System.out.println(i);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser() {
        int i = userMapper.updateStatus(150, 0);
        int i1 = userMapper.updateHeader(150, "http://www.nowcoder.com/111.png");
        int i2 = userMapper.updatePassword(150, "54645");
        System.out.println(i);
        System.out.println(i1);
        System.out.println(i2);
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;
    /**
     * List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
     */
    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for(DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int i = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(i);

    }
    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("test");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicket.setStatus(0);
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectByTicket() {
        String ticket = "test";
        LoginTicket loginTicket = loginTicketMapper.selectByTicket(ticket);
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus(ticket, 0);
        loginTicket = loginTicketMapper.selectByTicket(ticket);
        System.out.println(loginTicket);
    }

    @Test
    public void  testSelectMessages() {
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        System.out.println(messageMapper.selectConversationCount(111));
    }
}
