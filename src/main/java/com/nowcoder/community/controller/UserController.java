package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
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

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        String originalFilename = headerImage.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "/site/setting";
        }

        //?????????????????????????????????
        String fileName = CommunityUtil.generateUUID() + "." + suffix;
        //????????????????????????
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //????????????
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("????????????????????? " + e.getMessage());
            throw new RuntimeException("???????????????????????????????????????", e);
        }

        //?????????????????????????????????web???????????????
        //http:localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        //?????????????????????
        fileName = uploadPath + "/" + fileName;

        //??????????????????
        String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);

        //????????????
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
            logger.error("????????????????????? " + e.getMessage());
        }

    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, String confirmPassword,
                                 @CookieValue("ticket") String ticket, Model model) {
        LoginTicket loginTicket = userService.findLoginTicket(ticket);
        User user = userService.getUserById(loginTicket.getUserId());
        if (oldPassword.length() < 8 && !(CommunityUtil.md5(oldPassword+user.getSalt())).equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "????????????????????????8??????");
            return "site/setting";
        }
        if (newPassword.length() < 8) {
            model.addAttribute("newPasswordMsg", "????????????????????????8??????");
            return "site/setting";
        }
        //????????????????????????
        if (!(CommunityUtil.md5(oldPassword+user.getSalt())).equals(user.getPassword())) {
            model.addAttribute("wrongPasswordMsg", "??????????????????");
            return "site/setting";
        }
        //????????????????????????????????????
        if (!confirmPassword.equals(newPassword)) {
            model.addAttribute("confirmPasswordMsg", "??????????????????????????????!");
            return "site/setting";
        }
        //???????????????????????????
        if ((CommunityUtil.md5(newPassword+user.getSalt())).equals(user.getPassword())) {
            model.addAttribute("noChangePasswordMsg", "????????????????????????????????????????????????");
        }
        int i = userService.updatePassword(ticket, newPassword);

        return "redirect:/index";
    }

}
