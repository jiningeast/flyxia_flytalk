package com.flyxia.flytalk.handler.exception;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.handler.exceptionEnum.ExceptionEnum;
import com.flyxia.flytalk.handler.exceptionEnum.ExceptionProperties;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/4/25 17:58
 */
@ControllerAdvice
public class ExceptionHandler {
    @Autowired
    private ExceptionProperties exceptionProperties;

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(BaseException.class)
    public String handleException(BaseException e){
        int code = e.getErrorCode();
        Map<Integer,String> maps = exceptionProperties.getExceptionMaps();
        String message= maps.get(code);
        if (message!=null&&!"".equals(message.trim())){
            return JSON.toJSONString(new Result(false,new ExceptionEnum(code,message)));
        }else {
            return JSON.toJSONString(new Result(false,new ExceptionEnum(code,"内部错误！")));
        }
    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(BaseExceptionWithMessage.class)
    public String handleExceptionWithException(BaseExceptionWithMessage e){
        return JSON.toJSONString(new Result(false,new ExceptionEnum(1000,e.getMessage())));

    }

    @ResponseBody
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public String handleException(Exception e){
        return JSON.toJSONString(new Result(false,new ExceptionEnum(1000,e.getMessage())));

    }

}
