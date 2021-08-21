package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.dto.ResultDto;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.exception.CustomizeException;
import com.example.community.exception.ExceptionCode;
import com.example.community.model.Comment;
import com.example.community.model.User;
import com.example.community.service.CommentService;
import com.example.community.service.NotificationService;
import com.example.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @PostMapping("/comment")
    @ResponseBody
    public ResultDto addComment(@RequestBody Comment comment, HttpServletRequest request){
        //未登录会被拦截器拦截，所以不存在user为null的情况
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            throw new CustomizeException(ExceptionCode.NO_LOGIN);
        }
        if(StringUtils.isBlank(comment.getContent())){
            throw new CustomizeException(ExceptionCode.CONTENT_IS_EMPTY);
        }
        comment.setCommentatorId(user.getAccountId());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModify(comment.getGmtCreate());
        comment.setLikeCount(0);
        comment.setCommentCount(0);
        commentService.insertComment(comment,request);
        return new ResultDto(200,"评论成功");
    }

    @GetMapping("/comment/{id}")
    @ResponseBody
    public ResultDto<List<Comment>> getComments(@PathVariable("id")int id){
        List<Comment> comments = commentService.list(new QueryWrapper<Comment>().eq("parent_id", id).eq("type", CommentTypeEnum.COMMENT.getType()));
        for (Comment comment : comments) {
            comment.setUser(userService.getOne(new QueryWrapper<User>().eq("account_id", comment.getCommentatorId())));
        }
        return new ResultDto(200,"评论成功",comments);
    }
}
