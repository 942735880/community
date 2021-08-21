package com.example.community.enums;

public enum LikeStateEnum {
    DO_LIKE(0),
    CANCEL_LIKE(1);
    private int state;

    LikeStateEnum(int state){
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
