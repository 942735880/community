package com.example.community.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.community.enums.CommentTypeEnum;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Comment {
    private int id;
    private Integer parentId;
    private Integer type;
    private String commentatorId;
    private Long gmtCreate;
    private Long gmtModify;
    private Integer likeCount;
    private String content;
    private Integer commentCount;
    @TableField(exist = false)
    private User user;

    public boolean isExistType() {
        if(type == CommentTypeEnum.COMMENT.getType() || type == CommentTypeEnum.QUESTION.getType()){
            return true;
        }
        return false;
    }
}
