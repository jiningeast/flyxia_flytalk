package com.flyxia.flytalk.controller;

import com.flyxia.flytalk.handler.exception.BaseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author automannn@163.com
 * @time 2019/4/25 11:04
 */

@Controller
@RequestMapping("/test")
public class Hello {

    @ResponseBody
    @GetMapping("/hi")
    public String sayHi(){
        return "你好！ hello!";
    }


    @GetMapping("/exception")
    public String testException() throws Exception{
        throw new BaseException(3);
    }
}
