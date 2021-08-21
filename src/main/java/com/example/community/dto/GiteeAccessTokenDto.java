package com.example.community.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("gitee")
public class GiteeAccessTokenDto {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
}
