package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.enums.NotificationStateEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.model.Comment;
import com.example.community.model.Notification;
import com.example.community.service.CommentService;
import com.example.community.service.NotificationService;
import com.example.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @Autowired
    CommentService commentService;

    @GetMapping("/notification/{id}")
    public String toNotification(@PathVariable("id") int id, HttpServletRequest request){
        Notification notification = notificationService.getById(id);
        int type = notification.getType();
        int questionNo;
        if(type == NotificationTypeEnum.QUESTION_COMMENT.getType() || type == NotificationTypeEnum.QUESTION_LIKE.getType()
                || type == NotificationTypeEnum.QUESTION_CANCEL_LIKE.getType()){
            questionNo = notification.getOutId();
        }else{
            int parentId = notification.getOutId();
            Comment comment = commentService.getById(parentId);
            while(comment.getType() != 1){
                comment = commentService.getById(comment.getParentId());
            }
            questionNo = comment.getParentId();
        }
        notification.setState(NotificationStateEnum.ALREADY_READ.getState());
        notificationService.update(notification,new QueryWrapper<Notification>().eq("id",notification.getId()));
        int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
        request.getSession().setAttribute("unReadCount",unReadCount - 1);
        return "redirect:/question/" + questionNo;
    }
}
