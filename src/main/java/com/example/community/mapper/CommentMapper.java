package com.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.community.model.Comment;

public interface CommentMapper extends BaseMapper<Comment> {
    void updateCommentCount(Comment comment);

    void increaseCommentLike(Comment comment);

    void decreaseCommentLike(Comment comment);
}
