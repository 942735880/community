package com.example.community.dto;

import lombok.Data;

import java.util.List;

@Data
public class TagDto {
    private String categoryName;    //标签类名
    private List<String> tags;  //具体标签名
}
