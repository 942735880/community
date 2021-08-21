package com.example.community.advice;

import com.example.community.dto.ResultDto;
import com.example.community.exception.CustomizeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(CustomizeException.class)
    @ResponseBody
    public Object handleException(HttpServletRequest request, Throwable exception){
        String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            CustomizeException e = (CustomizeException) exception;
            return new ResultDto(e.getCode(),e.getMessage());
        }else{
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message",exception.getMessage());
            modelAndView.setViewName("error");
            return modelAndView;
        }
    }
}
