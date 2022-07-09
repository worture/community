package com.nowcoder.community.controller;

import com.nowcoder.community.pojo.LoginTicket;
import com.nowcoder.community.pojo.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import javafx.geometry.Pos;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "还没有选择图片！");
            return "/site/setting";
        }

        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        //生成随机文件名防止重复
        String fileName = CommunityUtil.generateUUID() + "." + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败： " + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！", e);
        }

        //更新当前用户头像路径（web访问路径）
        //http:localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //服务器存放路径
        fileName = uploadPath + "/" + fileName;

        //获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);

        //响应图片
        response.setContentType("image/" + suffix);
        try(
                ServletOutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(fileName);
        ) {
            byte[] buffer = new byte[1024];
            int index = 0;
            while ((index = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, index);
            }

        } catch (IOException e) {
            logger.error("读取图像失败： " + e.getMessage());
        }

    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, String confirmPassword,
                                 @CookieValue("ticket") String ticket, Model model) {
        LoginTicket loginTicket = userService.findLoginTicket(ticket);
        User user = userService.getUserById(loginTicket.getUserId());
        if (oldPassword.length() < 8 && !(CommunityUtil.md5(oldPassword+user.getSalt())).equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "密码长度不得小于8位！");
            return "site/setting";
        }
        if (newPassword.length() < 8) {
            model.addAttribute("newPasswordMsg", "密码长度不得小于8位！");
            return "site/setting";
        }
        //输入的原密码错误
        if (!(CommunityUtil.md5(oldPassword+user.getSalt())).equals(user.getPassword())) {
            model.addAttribute("wrongPasswordMsg", "原密码错误！");
            return "site/setting";
        }
        //新密码与确认新密码不一致
        if (!confirmPassword.equals(newPassword)) {
            model.addAttribute("confirmPasswordMsg", "两次输入的密码不一致!");
            return "site/setting";
        }
        //新密码与旧密码一致
        if ((CommunityUtil.md5(newPassword+user.getSalt())).equals(user.getPassword())) {
            model.addAttribute("noChangePasswordMsg", "新密码与原密码一致，请重新输入！");
        }
        int i = userService.updatePassword(ticket, newPassword);

        return "redirect:/index";
    }

}
