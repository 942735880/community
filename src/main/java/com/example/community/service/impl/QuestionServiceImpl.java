package com.example.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.mapper.QuestionMapper;
import com.example.community.model.Question;
import com.example.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl  extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Autowired
    QuestionMapper questionMapper;

    @Override
    public void updateViewCount(Question question) {
        questionMapper.updateViewCount(question);
    }

    @Override
    public void updateCommentCount(Question question) {
        questionMapper.updateCommentCount(question);
    }

    @Override
    public List<Question> getRelatedQuestions(Question question) {
        question.setTag(question.getTag().replace(',','|'));
        return questionMapper.getRelatedQuestions(question);
    }

    @Override
    public void increaseQuestionLike(Question question) {
        questionMapper.increaseQuestionLike(question);
    }

    @Override
    public void decreaseQuestionLike(Question question) {
        questionMapper.decreaseQuestionLike(question);
    }
}
