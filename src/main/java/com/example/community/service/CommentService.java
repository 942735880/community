package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.model.Comment;

import javax.servlet.http.HttpServletRequest;

public interface CommentService extends IService<Comment> {
    void insertComment(Comment comment, HttpServletRequest request);
    void updateCommentCount(Comment comment);
    void increaseCommentLike(Comment comment);
    void decreaseCommentLike(Comment comment);
}
