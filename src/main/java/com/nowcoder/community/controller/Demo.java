package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;

@Controller
@RequestMapping("/demo")
public class Demo {
    @RequestMapping("/hello")
    @ResponseBody
    public String simpleResp() {
        return "Hello.Spring Boot.";
    }

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/date")
    @ResponseBody
    public String date() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //获取请求数据
        System.out.println(req.getMethod());
        System.out.println(req.getServletPath());
        //迭代器
        Enumeration<String> enumeration = req.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = req.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(req.getParameter("code"));
        //返回响应数据
        PrintWriter writer = resp.getWriter();
        writer.write("<h1>NowCoder<h1>");
        writer.close();

    }
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    @RequestMapping(path = "/students/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@PathVariable("id") int id) {
        System.out.println(id);
        return "a stu";
    }

    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //响应html
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "zhangsan");
        modelAndView.addObject("age", "30");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
    @RequestMapping(path = "/teacherA", method = RequestMethod.GET)
    public String getTeacher(Model model) {
        model.addAttribute("name", "zhangsaaaaan");
        model.addAttribute("age", "30");
        return "/demo/view";
    }
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse httpServletResponse) {
        Cookie cookie = new Cookie("name", CommunityUtil.generateUUID());
        cookie.setPath("/community/demo");
        cookie.setMaxAge(60 * 10);
        httpServletResponse.addCookie(cookie);
        return "set cookie success";
    }
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("name") String code) {
        System.out.println(code);
        return "get cookie success";
    }

    //响应异步请求（JSON）
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0, "success!");
    }
}

