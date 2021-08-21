package com.example.community.model;

import lombok.Data;

@Data
public class ThumbsUp {
    private int id;
    private Integer parentId;
    private Integer type;
    private String likerId;
    private Long gmtCreate;
    private Long gmtModify;
    private Integer state;
}
