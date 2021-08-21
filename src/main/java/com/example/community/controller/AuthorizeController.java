package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.community.dto.GiteeAccessTokenDto;
import com.example.community.dto.GiteeUser;
import com.example.community.dto.GithubAccessTokenDto;
import com.example.community.dto.GitHubUser;
import com.example.community.model.User;
import com.example.community.provider.GitHubProvider;
import com.example.community.provider.GiteeProvider;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    GitHubProvider gitHubProvider;

    @Autowired
    GithubAccessTokenDto githubAccessTokenDto;

    @Autowired
    GiteeProvider giteeProvider;

    @Autowired
    GiteeAccessTokenDto giteeAccessTokenDto;

    @Autowired
    UserService userService;

    @GetMapping("/GitHubCallback")
    public String githubAuthorize(@RequestParam("code")String code,@RequestParam("state")String state,
                                  HttpServletRequest request,HttpServletResponse response){
        githubAccessTokenDto.setCode(code);
        githubAccessTokenDto.setState(state);
        String accessToken = gitHubProvider.getAccessToken(githubAccessTokenDto);
        GitHubUser loginUser = gitHubProvider.getUser(accessToken);
        if(loginUser != null){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id",String.valueOf(loginUser.getId()));
            User userExist = userService.getOne(queryWrapper);  //根据accountId在数据库中查找用户
            if(userExist == null){
                //首次登录，用户不存在，添加到数据库中
                User user = new User();
                user.setName(loginUser.getName());
                user.setAccountId(String.valueOf(loginUser.getId()));
                String token = UUID.randomUUID().toString();
                user.setToken(token);
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModify(user.getGmtCreate());
                user.setAvatarUrl(loginUser.getAvatarUrl());
                userService.save(user);
                Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(60 * 60 * 24);
                response.addCookie(cookie);  //响应中添加cookie，浏览器下次请求会携带该cookie
            }else{
                //用户已存在，再次授权登录，修改用户名、登录时间、头像链接(用户名和头像链接从授权用户获取)
                userExist.setName(loginUser.getName());
                userExist.setGmtModify(System.currentTimeMillis());
                userExist.setAvatarUrl(loginUser.getAvatarUrl());
                userService.update(userExist,null);
                Cookie cookie = new Cookie("token", userExist.getToken());
                cookie.setMaxAge(60 * 60 * 24);
                response.addCookie(cookie);  //响应中添加cookie，浏览器下次请求会携带该cookie
            }
        }
        return "redirect:/";
    }

    @GetMapping("/GiteeCallback")
    public String giteeAuthorize(@RequestParam("code")String code, HttpServletResponse response){
        giteeAccessTokenDto.setCode(code);
        String accessToken = giteeProvider.getAccessToken(giteeAccessTokenDto); //获取accessToken
        GiteeUser loginUser = giteeProvider.getUser(accessToken);   //获取授权user信息
        if(loginUser != null){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("account_id",String.valueOf(loginUser.getId()));
            User userExist = userService.getOne(queryWrapper);  //根据accountId在数据库中查找用户
            if(userExist == null){
                //首次登录，用户不存在，添加到数据库中
                User user = new User();
                user.setName(loginUser.getName());
                user.setAccountId(String.valueOf(loginUser.getId()));
                String token = UUID.randomUUID().toString();
                user.setToken(token);
                user.setGmtCreate(System.currentTimeMillis());
                user.setGmtModify(user.getGmtCreate());
                user.setAvatarUrl(loginUser.getAvatarUrl());
                userService.save(user);
                Cookie cookie = new Cookie("token", token);
                cookie.setMaxAge(60 * 60 * 24);
                response.addCookie(cookie);  //响应中添加cookie，浏览器下次请求会携带该cookie
            }else{
                //用户已存在，再次授权登录，修改用户名、登录时间、头像链接(用户名和头像链接从授权用户获取)
                userExist.setName(loginUser.getName());
                userExist.setGmtModify(System.currentTimeMillis());
                userExist.setAvatarUrl(loginUser.getAvatarUrl());
                userService.update(userExist,new QueryWrapper<User>().eq("account_id",userExist.getAccountId()));
                Cookie cookie = new Cookie("token", userExist.getToken());
                cookie.setMaxAge(60 * 60 * 24);
                response.addCookie(cookie);  //响应中添加cookie，浏览器下次请求会携带该cookie
            }
        }
        return "redirect:/";
    }
}
