package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.cache.TagCache;
import com.example.community.model.Question;
import com.example.community.model.User;
import com.example.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {
    @Autowired
    QuestionService questionService;

    //访问发布页面
    @GetMapping("/publish")
    public String toPublish(Model model){
        model.addAttribute("tags",TagCache.getTagList());
        return "publish";
    }

    //发帖
    @PostMapping("/publish")
    public String doPublish(@RequestParam("title") String title, @RequestParam("description") String description,
                            @RequestParam("tag") String tag, @RequestParam(value = "id",defaultValue = "-1") Integer id,
                            HttpServletRequest request, Model model){
        model.addAttribute("title",title);  //数据回显
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        model.addAttribute("tags",TagCache.getTagList());
        if(title == null || "".equals(title)){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description == null || "".equals(description)){
            model.addAttribute("error","描述不能为空");
            return "publish";
        }
        if(tag== null || "".equals(tag)){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        if(!StringUtils.isBlank(TagCache.filterInvalid(tag))){
            model.addAttribute("error","存在不合法标签" + TagCache.filterInvalid(tag));
            return "publish";
        }
        User user = (User) request.getSession().getAttribute("user");
        if(user != null){
            //已登陆
            //当发帖时，默认id值为-1，返回值为null，添加帖子；更新帖子时，携带id值，返回值不为null
            Question question = questionService.getOne(new QueryWrapper<Question>().eq("id", id));
            if(question == null){
                //根据id查询帖子，返回值为null,发布帖子
                question = new Question();
                question.setTitle(title);
                question.setDescription(description);
                question.setTag(tag);
                question.setViewCount(0);
                question.setCommentCount(0);
                question.setLikeCount(0);
                question.setGmtCreate(System.currentTimeMillis());
                question.setGmtModify(question.getGmtCreate());
                question.setCreatorId(user.getAccountId());
                questionService.save(question);
            }else{
                //根据id查询帖子，返回值为不为null,更新帖子
                question.setTitle(title);
                question.setDescription(description);
                question.setTag(tag);
                question.setGmtModify(System.currentTimeMillis());
                questionService.update(question,new QueryWrapper<Question>().eq("id",id));
            }
            return "redirect:/";
        }else{
            //登录失败
            model.addAttribute("error","请先登录，才能发布帖子");
            return "publish";
        }
    }

    //更新帖子
    @GetMapping("/publish/{id}")
    public String update(@PathVariable(name = "id") Integer id,Model model){
        Question question = questionService.getOne(new QueryWrapper<Question>().eq("id", id));
        model.addAttribute("id",id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("tags",TagCache.getTagList());
        return "/publish";
    }
}
