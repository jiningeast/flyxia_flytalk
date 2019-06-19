package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.service.api.UserService;
import com.flyxia.flytalk.util.ObjectUtil;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


/**
 * @author automannn@163.com
 * @time 2019/4/25 12:03
 * 正常运行的代码，均正确。
 * 若有逻辑异常的代码，则用特定的异常处理快处理
 */
@Controller
@RequestMapping("/user")
@ResponseBody
public class UserApi {
    @Autowired
    private UserService userService;


    //注册
    // 参数: 电话号码，用户名，密码
    @PostMapping("/register")
    public String logOn(@RequestBody User u) throws BaseException {
        if (u.getPhoneNumber()==null) throw new BaseException(300);
        //用户注册时，默认其用户名就是电话号码
        if(u.getUsername()==null) u.setUsername(u.getPhoneNumber());

       SimpleBaseDto simpleBaseDto= (SimpleBaseDto) userService.logOn(u);
       User user = (User) simpleBaseDto.getTarget();
        return JSON.toJSONString(new Result(true,user));
    }



    //查询用户
    @PostMapping("/query")
    public String query(String userId,String username,String phoneNumber) throws BaseException {
        //查询单个用户，至少包括电话号码，或者 用户名
        if (username==null&&userId==null&&phoneNumber==null) throw new BaseException(501);
        User u = new User();
        if (userId!=null) u.setId(userId);
        if (username!=null) u.setUsername(username);
        if (phoneNumber!=null) u.setPhoneNumber(phoneNumber);


        SimpleBaseDto simpleBaseDto= (SimpleBaseDto) userService.query(u);
        User user = (User) simpleBaseDto.getTarget();

        return JSON.toJSONString(new Result(true,user));
    }

    @PostMapping("/login")
    public String logIn(String username,String password, String phoneNumber) throws BaseException {
        //登陆时，必传： 用户名+密码  或者 电话号码+密码
        if (username==null&&phoneNumber==null) throw new BaseException(401);
        if (password==null) throw new BaseException(402);

        User u = new User();
        if (phoneNumber!=null)u.setPhoneNumber(phoneNumber);
        if (username!=null)u.setUsername(username);
        u.setPassword(password);
        SimpleBaseDto simpleBaseDto= (SimpleBaseDto) userService.login(u);
        User user = (User) simpleBaseDto.getTarget();

        return JSON.toJSONString(new Result(true,user));
    }

    //查询所有用户
    @GetMapping("/queryAll")
    public String queryAll(){
        SimpleBaseDto simpleBaseDto= (SimpleBaseDto) userService.queryAll();
        List list =  simpleBaseDto.getTargetList();
        return JSON.toJSONString(new Result(true,list));
    }

    //修改用户头像;   获取用户头像，通过filter配置的
    @PostMapping("/avatar")
    public String setAvatar(MultipartFile file,String userId) throws Exception {
        if (userId==null||file ==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        SimpleBaseDto dto= (SimpleBaseDto) userService.setAvatar(file,userId);
        if (dto.getTarget()==null) return JSON.toJSONString(new Result(false,"上传失败！"));
        return JSON.toJSONString(new Result(true,"上传成功！"));
    }

}
