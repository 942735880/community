package com.example.community.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.community.enums.NotificationTypeEnum;
import com.example.community.exception.CustomizeException;
import com.example.community.exception.ExceptionCode;
import com.example.community.model.Notification;
import com.example.community.model.Question;
import com.example.community.model.User;
import com.example.community.service.NotificationService;
import com.example.community.service.QuestionService;
import com.example.community.service.UserService;
import com.example.community.utils.PageNavUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ProfileController {
    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String toProfile(@PathVariable("action") String action, Model model,
                            @RequestParam(value = "pageNo",defaultValue = "1") int pageNo, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        if(user == null){
            throw new CustomizeException(ExceptionCode.NO_LOGIN);
        }
        if("questions".equals(action)){   //如果请求中携带questions，则显示该用户的帖子
            Page<Question> page = questionService.page(new Page<>(pageNo,4), new QueryWrapper<Question>().eq("creator_id",user.getAccountId())); //page中的question为从数据库中查到的，并没有将对应的user封装进去
            List<Question> questions = page.getRecords();
            for (Question question : questions) {
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("account_id",question.getCreatorId());
                User questionUser = userService.getOne(userQueryWrapper);
                question.setUser(questionUser);
            }
            int[] nums = PageNavUtils.pageNav(pageNo, (int) page.getPages(), 5);    //获取分页条信息
            model.addAttribute("section","questions");  //负责显示高亮
            model.addAttribute("sectionName","我的帖子");   //负责显示名称
            model.addAttribute("page",page);
            model.addAttribute("nums",nums);
        }else if("replies".equals(action)){   //如果请求中携带questions，则显示该用户的最新回复
            Page<Notification> notificationPage = notificationService.page(new Page<>(pageNo,10), new QueryWrapper<Notification>().eq("receiver_id", user.getAccountId()).orderByAsc("state"));
            List<Notification> notifications = notificationPage.getRecords();
            for (Notification notification : notifications) {
                if(notification.getType() == 1){
                    notification.setTypeName(NotificationTypeEnum.QUESTION_COMMENT.getMessage());
                }else if(notification.getType() == 2){
                    notification.setTypeName(NotificationTypeEnum.COMMENT_COMMENT.getMessage());
                }else if(notification.getType() == 3){
                    notification.setTypeName(NotificationTypeEnum.QUESTION_LIKE.getMessage());
                }else if(notification.getType() == 4){
                    notification.setTypeName(NotificationTypeEnum.COMMENT_LIKE.getMessage());
                }else if(notification.getType() == 5){
                    notification.setTypeName(NotificationTypeEnum.QUESTION_CANCEL_LIKE.getMessage());
                }else if(notification.getType() == 6){
                    notification.setTypeName(NotificationTypeEnum.COMMENT_CANCEL_LIKE.getMessage());
                }
            }
            int[] nums = PageNavUtils.pageNav(pageNo, (int) notificationPage.getPages(), 5);    //获取分页条信息
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
            model.addAttribute("notificationPage",notificationPage);
            model.addAttribute("nums",nums);
        }
        return "profile";
    }
}
