package com.example.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.enums.CommentTypeEnum;
import com.example.community.enums.NotificationStateEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeException;
import com.example.community.exception.ExceptionCode;
import com.example.community.mapper.CommentMapper;
import com.example.community.model.Comment;
import com.example.community.model.Notification;
import com.example.community.model.Question;
import com.example.community.model.User;
import com.example.community.service.CommentService;
import com.example.community.service.NotificationService;
import com.example.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    CommentMapper commentMapper;

    @Transactional
    @Override
    public void insertComment(Comment comment, HttpServletRequest request) {
        if(comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(ExceptionCode.TARGET_PARAM_NOT_FOUND);
        }
        if(comment.getType() == null || !comment.isExistType()){
            throw new CustomizeException(ExceptionCode.TYPE_PARAM_WRONG);
        }
        Comment dbComment = getOne(new QueryWrapper<Comment>().eq("id",comment.getParentId())); //根据id获取评论
        Question question = questionService.getOne(new QueryWrapper<Question>().eq("id",comment.getParentId()));    //根据id获取问题
        User user = (User) request.getSession().getAttribute("user");
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论
            if (dbComment == null) {
                throw new CustomizeException(ExceptionCode.COMMENT_NOT_FOUND);
            }
            updateCommentCount(dbComment);
            //添加评论、更新评论数后，创建通知
            Notification notification = new Notification();
            notification.setNotifierId(user.getAccountId());    //通知人id为当前界面用户的id
            notification.setReceiverId(dbComment.getCommentatorId());   //接收人id为评论创建者的id
            notification.setOutId(dbComment.getId());   //通过outId和type锁定了是对象是问题或评论
            notification.setType(NotificationTypeEnum.COMMENT_COMMENT.getType());
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setState(NotificationStateEnum.NOT_READ.getState());   //创建的通知初始状态均为未读
            notification.setNotifierName(user.getName());
            notification.setReceiverName(dbComment.getContent());
            notificationService.save(notification);
            int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
            request.getSession().setAttribute("unReadCount",unReadCount + 1);   //未读数 + 1
        }
        else {
            // 回复问题
            if (question == null) {
                throw new CustomizeException(ExceptionCode.QUESTION_NOT_FOUND);
            }
            questionService.updateCommentCount(question);
            //添加评论、更新评论数后，创建通知
            Notification notification = new Notification();
            notification.setNotifierId(user.getAccountId());
            notification.setReceiverId(question.getCreatorId());
            notification.setOutId(question.getId());
            notification.setType(NotificationTypeEnum.QUESTION_COMMENT.getType());
            notification.setGmtCreate(System.currentTimeMillis());
            notification.setState(NotificationStateEnum.NOT_READ.getState());
            notification.setNotifierName(user.getName());
            notification.setReceiverName(question.getTitle());
            notificationService.save(notification);
            int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
            request.getSession().setAttribute("unReadCount",unReadCount + 1);   //未读数 + 1
        }
        save(comment);
    }

    @Override
    public void updateCommentCount(Comment comment) {
        commentMapper.updateCommentCount(comment);
    }

    @Override
    public void increaseCommentLike(Comment comment) {
        commentMapper.increaseCommentLike(comment);
    }

    @Override
    public void decreaseCommentLike(Comment comment) {
        commentMapper.decreaseCommentLike(comment);
    }
}
