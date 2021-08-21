package com.example.community.enums;

public enum NotificationTypeEnum {
    QUESTION_COMMENT(1,"评论了问题"),
    COMMENT_COMMENT(2,"评论了评论"),
    QUESTION_LIKE(3,"点赞了问题"),
    COMMENT_LIKE(4,"点赞了评论"),
    QUESTION_CANCEL_LIKE(5,"取赞了问题"),
    COMMENT_CANCEL_LIKE(6,"取赞了评论");

    private int type;
    private String message;

    NotificationTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
