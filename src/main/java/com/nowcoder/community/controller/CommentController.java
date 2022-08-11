package com.nowcoder.community.controller;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.pojo.Comment;
import com.nowcoder.community.pojo.DiscussPost;
import com.nowcoder.community.pojo.Event;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件

        Event event = new Event()
            .setTopic(TOPIC_COMMENT)
            .setUserId(hostHolder.getUser().getId())
            .setEntityType(comment.getEntityType())
            .setEntityId(comment.getEntityId())
            .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost discussPost = discussPostMapper.selecDiscussPostById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }
        else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment targer = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(targer.getUserId());
        }

        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }

}
