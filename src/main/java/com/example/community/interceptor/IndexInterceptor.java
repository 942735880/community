package com.example.community.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.community.enums.NotificationStateEnum;
import com.example.community.model.Notification;
import com.example.community.model.User;
import com.example.community.service.NotificationService;
import com.example.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class IndexInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if("token".equals(cookie.getName())){
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("token",cookie.getValue());
                    User user = userService.getOne(queryWrapper);
                    if(user != null){
                        user.setGmtModify(System.currentTimeMillis());  //再次登录修改登录时间
                        userService.update(user,null);  //将修改的时间写入数据库中
                        request.getSession().setAttribute("user",user);
                        cookie.setMaxAge(60 * 60 * 24);    //每次登录都重置cookie保存时间
                        List<Notification> notifications = notificationService.list(new QueryWrapper<Notification>().eq("receiver_id",user.getAccountId()).eq("state", NotificationStateEnum.NOT_READ.getState()));
                        request.getSession().setAttribute("unReadCount",notifications.size());
                    }
                    break;
                }
            }
        }
        return true;
    }
}
