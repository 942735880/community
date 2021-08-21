package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.model.Question;
import com.example.community.model.User;
import com.example.community.service.QuestionService;
import com.example.community.service.UserService;
import com.example.community.utils.PageNavUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sun.misc.Regexp;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String toIndex(HttpServletRequest request, Model model,
                          @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                          @RequestParam(value = "questionName",required = false)String questionName){
        //先将问题按发布时间倒叙排列，再进行分页显示
        Page<Question> page;
        if(questionName == null){
            page = questionService.page(new Page<>(pageNo,4),new QueryWrapper<Question>().orderByDesc("gmt_create"));  //page中的question为从数据库中查到的，并没有将对应的user封装进去
        }else{
            questionName = questionName.replace(" ", "|");
            String[] splits = questionName.split("\\|");
            QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
            for (int i = 0; i < splits.length - 1; i++) {
                queryWrapper.like("title",splits[i]);
                queryWrapper.or();
            }
            queryWrapper.like("title",splits[splits.length - 1]);
            page = questionService.page(new Page<>(pageNo,4),queryWrapper.orderByDesc("gmt_create"));  //page中的question为从数据库中查到的，并没有将对应的user封装进去
        }
        List<Question> questions = page.getRecords();
        int[] nums = PageNavUtils.pageNav(pageNo, (int) page.getPages(), 5);  //获取分页条信息
        for (Question question : questions) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id",question.getCreatorId());
            User user = userService.getOne(queryWrapper);
            question.setUser(user);
        }
        model.addAttribute("page",page);
        model.addAttribute("nums",nums);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        request.getSession().invalidate();
        /*注销session，否则即使删除cookie，同一次会话中session中的user仍然存在，存在安全问题，
         如即使注销，但user仍存在session中，渲染页面时仍处于登录状态*/
        Cookie token = new Cookie("token", null);
        token.setMaxAge(0);
        response.addCookie(token);  //覆盖请求中的同名cookie，设置其存活时间为0，达到删除token的目的
        return "redirect:/";
    }
}
