package com.example.community.enums;

public enum LikeTypeEnum {
    LIKE_QUESTION(1),
    LIKE_COMMENT(2);

    private int type;

    LikeTypeEnum(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
