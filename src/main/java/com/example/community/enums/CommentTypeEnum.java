package com.example.community.enums;

public enum CommentTypeEnum {
    QUESTION(1),
    COMMENT(2);

    int type;

    CommentTypeEnum(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
