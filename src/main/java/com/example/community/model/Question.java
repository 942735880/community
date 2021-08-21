package com.example.community.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Question {
    private int id;
    private String title;
    private String description;
    private Long gmtCreate;
    private Long gmtModify;
    private String creatorId;
    private Integer commentCount;
    private Integer viewCount;
    private Integer likeCount;
    private String tag;
    @TableField(exist = false)
    private User user;
}
