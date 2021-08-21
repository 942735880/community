package com.example.community.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Notification {
    private int id;
    private String notifierId;
    private String receiverId;
    private int outId;
    private int type;
    private Long gmtCreate;
    private int state;
    private String notifierName;
    private String receiverName;
    @TableField(exist = false)
    private String typeName;
}
