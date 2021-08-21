package com.example.community.enums;

public enum NotificationStateEnum {
    NOT_READ(0),
    ALREADY_READ(1);

    private int state;

    NotificationStateEnum(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
