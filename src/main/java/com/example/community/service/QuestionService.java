package com.example.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.community.model.Question;

import java.util.List;

public interface QuestionService extends IService<Question> {
    void updateViewCount(Question question);
    void updateCommentCount(Question question);
    List<Question> getRelatedQuestions(Question question);
    void increaseQuestionLike(Question question);
    void decreaseQuestionLike(Question question);
}
