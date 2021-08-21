package com.example.community.dto;

import com.example.community.model.Comment;
import lombok.Data;

import java.util.List;

@Data
public class ResultDto<T> {
    private Integer code;
    private String message;
    private T data;

    public ResultDto(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDto(Integer code,String message,T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
