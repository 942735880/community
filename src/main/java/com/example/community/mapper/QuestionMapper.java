package com.example.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.community.model.Question;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {
    void updateViewCount(Question question);

    void updateCommentCount(Question question);

    List<Question> getRelatedQuestions(Question question);

    void increaseQuestionLike(Question question);

    void decreaseQuestionLike(Question question);
}
