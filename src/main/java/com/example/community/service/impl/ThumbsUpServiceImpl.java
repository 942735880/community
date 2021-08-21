package com.example.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.community.mapper.ThumbsUpMapper;
import com.example.community.model.ThumbsUp;
import com.example.community.service.ThumbsUpService;
import org.springframework.stereotype.Service;

@Service
public class ThumbsUpServiceImpl extends ServiceImpl<ThumbsUpMapper, ThumbsUp> implements ThumbsUpService {
}
