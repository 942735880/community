package com.example.community;

import com.example.community.dto.GiteeAccessTokenDto;
import com.example.community.dto.GithubAccessTokenDto;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({GithubAccessTokenDto.class,GiteeAccessTokenDto.class})
@MapperScan("com.example.community.mapper")
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }
}
