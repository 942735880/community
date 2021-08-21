package com.example.community.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GiteeUser {
    private String name;
    private Long id;
    private String bio;
    private String avatarUrl;
}
