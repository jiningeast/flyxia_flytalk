package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.constants.SecurityConstants;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exceptionEnum.ExceptionEnum;
import com.flyxia.flytalk.security.processor.ValidateCodeProcessorHolder;
import com.flyxia.flytalk.service.api.UserService;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author automannn@163.com
 * @time 2019/4/27 13:45
 */
@RestController
public class ValidateCodeController {
    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Autowired
    private UserService userService;

    //请求验证码接口
    @PostMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/{type}")
    public String createCode(HttpServletRequest request, HttpServletResponse response, @PathVariable String type) throws Exception {
        String mobile = ServletRequestUtils.getRequiredStringParameter(request,"phoneNumber");
        User u= new User();
        u.setPhoneNumber(mobile);
        try {
            userService.query(u);
        }catch (BaseException e){
            validateCodeProcessorHolder.findValidateCodeProcessor(type).create(new ServletWebRequest(request,response));
            return JSON.toJSONString(new Result(true,"验证码已发送！"));
        }
        //若正常运行，表明该用户已经存在。
        return JSON.toJSONString(new Result(false,new ExceptionEnum(1000,"该用户已经注册！")));

    }

    //验证验证码接口
    @PostMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_MOBILE_VALIDATE)
    public String validateCode()throws Exception{
        return JSON.toJSONString(new Result(true,"验证成功！"));
    }

}
