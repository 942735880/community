package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.community.dto.ResultDto;
import com.example.community.enums.LikeStateEnum;
import com.example.community.enums.LikeTypeEnum;
import com.example.community.enums.NotificationStateEnum;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeException;
import com.example.community.exception.ExceptionCode;
import com.example.community.model.*;
import com.example.community.service.CommentService;
import com.example.community.service.NotificationService;
import com.example.community.service.ThumbsUpService;
import com.example.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ThumbsUpController {
    @Autowired
    ThumbsUpService thumbsUpService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    NotificationService notificationService;

    @ResponseBody
    @PostMapping("like")
    public ResultDto doLike(@RequestBody ThumbsUp thumbsUp, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            throw new CustomizeException(ExceptionCode.NO_LOGIN);
        }else{
            if(thumbsUp.getType() == LikeTypeEnum.LIKE_QUESTION.getType()){
                //对象为问题
                Question question = questionService.getOne(new QueryWrapper<Question>().eq("id", thumbsUp.getParentId()));
                if(question == null){
                    throw new CustomizeException(ExceptionCode.QUESTION_NOT_FOUND);
                }else{
                    if(thumbsUp.getState() == LikeStateEnum.DO_LIKE.getState()){
                        //点赞，判断该点赞是否之前存在，若存在修改时间，若不存在则创建点赞，增加问题点赞数
                        ThumbsUp thumbsUpExist = thumbsUpService.getOne(new QueryWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId())
                                .eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                        if(thumbsUpExist == null){
                            //保存点赞
                            thumbsUp.setLikerId(user.getAccountId());
                            thumbsUp.setGmtCreate(System.currentTimeMillis());
                            thumbsUp.setGmtModify(System.currentTimeMillis());
                            thumbsUp.setState(LikeStateEnum.CANCEL_LIKE.getState());    //设置状态为取消态，再次点击时，方便下次点击取消点赞
                            thumbsUpService.save(thumbsUp);
                        }else{
                            //点赞存在，修改时间，状态
                            thumbsUp.setGmtModify(System.currentTimeMillis());
                            thumbsUp.setState(LikeStateEnum.CANCEL_LIKE.getState());
                            thumbsUpService.update(thumbsUp,new UpdateWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                        }
                        questionService.increaseQuestionLike(question);
                        //首次点赞或再次点赞问题时，创建通知
                        Notification notification = new Notification();
                        notification.setNotifierId(user.getAccountId());
                        notification.setReceiverId(question.getCreatorId());
                        notification.setOutId(question.getId());
                        notification.setType(NotificationTypeEnum.QUESTION_LIKE.getType());
                        notification.setGmtCreate(System.currentTimeMillis());
                        notification.setState(NotificationStateEnum.NOT_READ.getState());
                        notification.setNotifierName(user.getName());
                        notification.setReceiverName(question.getTitle());
                        notificationService.save(notification);
                        int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
                        request.getSession().setAttribute("unReadCount",unReadCount + 1);   //未读数 + 1
                        return new ResultDto(200,"问题点赞成功！！！",thumbsUp);
                    }else{
                        //删除点赞，减少该问题的点赞数
                        ThumbsUp thumbsUpExist = thumbsUpService.getOne(new QueryWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                        if(thumbsUpExist == null){
                            throw new CustomizeException(ExceptionCode.LIKE_NOT_FOUNT);
                        }else{
                            thumbsUp.setGmtModify(System.currentTimeMillis());
                            thumbsUp.setState(LikeStateEnum.DO_LIKE.getState());
                            thumbsUpService.update(thumbsUp,new UpdateWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                            questionService.decreaseQuestionLike(question);
                        }
                        Notification notification = new Notification();
                        notification.setNotifierId(user.getAccountId());
                        notification.setReceiverId(question.getCreatorId());
                        notification.setOutId(question.getId());
                        notification.setType(NotificationTypeEnum.QUESTION_CANCEL_LIKE.getType());
                        notification.setGmtCreate(System.currentTimeMillis());
                        notification.setState(NotificationStateEnum.NOT_READ.getState());
                        notification.setNotifierName(user.getName());
                        notification.setReceiverName(question.getTitle());
                        notificationService.save(notification);
                        int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
                        request.getSession().setAttribute("unReadCount",unReadCount + 1);   //未读数 + 1
                        return new ResultDto(200,"问题取消点赞成功！！！",thumbsUp);
                    }
                }
            }
            else{
                //对象为评论
                Comment comment = commentService.getOne(new QueryWrapper<Comment>().eq("id", thumbsUp.getParentId()));
                if(comment == null){
                    throw new CustomizeException(ExceptionCode.COMMENT_NOT_FOUND);
                }else{
                    if(thumbsUp.getState() == LikeStateEnum.DO_LIKE.getState()){
                        //点赞，判断该点赞是否之前存在，若存在修改时间，若不存在则创建点赞
                        ThumbsUp thumbsUpExist = thumbsUpService.getOne(new QueryWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                        if(thumbsUpExist == null){
                            //保存点赞，增加该问题的点赞数
                            thumbsUp.setLikerId(user.getAccountId());
                            thumbsUp.setGmtCreate(System.currentTimeMillis());
                            thumbsUp.setGmtModify(System.currentTimeMillis());
                            thumbsUp.setState(LikeStateEnum.CANCEL_LIKE.getState());    //设置状态为取消态，再次点击时，方便下次点击取消点赞
                            thumbsUpService.save(thumbsUp);
                        }else{
                            //点赞存在，修改时间，状态
                            thumbsUp.setGmtModify(System.currentTimeMillis());
                            thumbsUp.setState(LikeStateEnum.CANCEL_LIKE.getState());
                            thumbsUpService.update(thumbsUp,new UpdateWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                        }
                        commentService.increaseCommentLike(comment);
                        //首次点赞或再次点赞评论时，创建通知
                        Notification notification = new Notification();
                        notification.setNotifierId(user.getAccountId());    //通知人id为当前界面用户的id
                        notification.setReceiverId(comment.getCommentatorId());   //接收人id为评论创建者的id
                        notification.setOutId(comment.getId());   //通过outId和type锁定了是对象是问题或评论
                        notification.setType(NotificationTypeEnum.COMMENT_LIKE.getType());
                        notification.setGmtCreate(System.currentTimeMillis());
                        notification.setState(NotificationStateEnum.NOT_READ.getState());   //创建的通知初始状态均为未读
                        notification.setNotifierName(user.getName());
                        notification.setReceiverName(comment.getContent());
                        notificationService.save(notification);
                        int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
                        request.getSession().setAttribute("unReadCount",unReadCount + 1);   //未读数 + 1
                        return new ResultDto(200,"评论点赞成功！！！",thumbsUp);
                    }else{
                        //删除点赞，减少该问题的点赞数
                        ThumbsUp thumbsUpExist = thumbsUpService.getOne(new QueryWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                        if(thumbsUpExist == null){
                            throw new CustomizeException(ExceptionCode.LIKE_NOT_FOUNT);
                        }else{
                            thumbsUp.setGmtModify(System.currentTimeMillis());
                            thumbsUp.setState(LikeStateEnum.DO_LIKE.getState());
                            thumbsUpService.update(thumbsUp,new UpdateWrapper<ThumbsUp>().eq("parent_id", thumbsUp.getParentId()).eq("type", thumbsUp.getType()).eq("liker_id", user.getAccountId()));
                            commentService.decreaseCommentLike(comment);
                        }
                        Notification notification = new Notification();
                        notification.setNotifierId(user.getAccountId());    //通知人id为当前界面用户的id
                        notification.setReceiverId(comment.getCommentatorId());   //接收人id为评论创建者的id
                        notification.setOutId(comment.getId());   //通过outId和type锁定了是对象是问题或评论
                        notification.setType(NotificationTypeEnum.COMMENT_CANCEL_LIKE.getType());
                        notification.setGmtCreate(System.currentTimeMillis());
                        notification.setState(NotificationStateEnum.NOT_READ.getState());   //创建的通知初始状态均为未读
                        notification.setNotifierName(user.getName());
                        notification.setReceiverName(comment.getContent());
                        notificationService.save(notification);
                        int unReadCount = (Integer) request.getSession().getAttribute("unReadCount");
                        request.getSession().setAttribute("unReadCount",unReadCount + 1);   //未读数 + 1
                        return new ResultDto(200,"评论取消点赞成功！！！",thumbsUp);
                    }
                }
            }
        }
    }
}
