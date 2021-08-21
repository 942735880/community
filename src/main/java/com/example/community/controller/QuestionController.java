package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.exception.CustomizeException;
import com.example.community.exception.ExceptionCode;
import com.example.community.model.Comment;
import com.example.community.model.Question;
import com.example.community.model.ThumbsUp;
import com.example.community.model.User;
import com.example.community.service.CommentService;
import com.example.community.service.QuestionService;
import com.example.community.service.ThumbsUpService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    ThumbsUpService thumbsUpService;

    //查看帖子详细信息
    @GetMapping("/question/{id}")
    public String toQuestion(@PathVariable(name = "id") Integer id, Model model, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        Question question = questionService.getOne(new QueryWrapper<Question>().eq("id",id));
        if(question == null){
            throw new CustomizeException(ExceptionCode.QUESTION_NOT_FOUND);
        }else{
            question.setUser(userService.getOne(new QueryWrapper<User>().eq("account_id", question.getCreatorId())));
            List<Comment> comments = commentService.list(new QueryWrapper<Comment>().eq("parent_id", question.getId()).eq("type", CommentTypeEnum.QUESTION.getType()));
            HashMap<Comment, ThumbsUp> map = new LinkedHashMap<>();
            for (Comment comment : comments) {
                comment.setUser(userService.getOne(new QueryWrapper<User>().eq("account_id", comment.getCommentatorId())));
                ThumbsUp commentThumbsUp = thumbsUpService.getOne(new QueryWrapper<ThumbsUp>().eq("parent_id", comment.getId()).eq("type", 2).eq("liker_id", user == null ? null :user.getAccountId()));
                map.put(comment,commentThumbsUp);
            }
            List<Question> relatedQuestions = questionService.getRelatedQuestions(question);
            ThumbsUp questionThumbsUp = thumbsUpService.getOne(new QueryWrapper<ThumbsUp>().eq("parent_id", id).eq("type", 1).eq("liker_id", user == null ? null : user.getAccountId()));
            List<ThumbsUp> commentThumbsUps = new ArrayList<>();
            model.addAttribute("question",question);    //问题
            model.addAttribute("questionThumbsUp",questionThumbsUp);    //问题点赞情况
            model.addAttribute("map",map);  //评论和点赞的map
            model.addAttribute("relatedQuestions",relatedQuestions);  //相关问题
            questionService.updateViewCount(question);
            return "question";
        }
    }
}
