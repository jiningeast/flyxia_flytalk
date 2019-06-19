package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.dao.UserSecurityDao;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.entity.UserSecurity;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.UserService;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author automannn@163.com
 * @time 2019/5/3 15:08
 */

@Controller
@RequestMapping("/account")
public class UserAccountApi {
    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityDao userSecurityDao;

    //获取公钥
    @PostMapping("/getpubkey")
    @ResponseBody
    public String getPublicKey(String userId){
        if (userId==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        UserSecurity us =  userSecurityDao.findOne(userId);
        if (us==null) return JSON.toJSONString(new Result(false,"用户信息不存在!"));
        return JSON.toJSONString(new Result(true,us.getPublicKey()));
    }



    //获取私钥
    @PostMapping("/getprikey")
    public void getPrivacyKey(String userId, HttpServletResponse response) throws BaseExceptionWithMessage {
        response.setContentType("multipart/form-data");
        if(userId==null) throw new BaseExceptionWithMessage("参数非法");
        UserSecurity us =  userSecurityDao.findOne(userId);
        if (us==null) throw new BaseExceptionWithMessage("用户信息存在!");
        String priKey = us.getPrivacyKey();
        try {
            OutputStream output = response.getOutputStream();
            byte[] pks= priKey.getBytes();
            int len = -1;
            output.write(pks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "attachment;fileName="+userId+"_privacykey.txt");
    }

    //余额更改。  客户端使用公钥加密，服务端使用用户的私钥验签
    @PostMapping("/amountupdate")
    @ResponseBody
    public String amountChange(String userId, String secretString,String md5) throws BaseExceptionWithMessage {
        if (secretString==null||userId==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        SimpleBaseDto sbd = (SimpleBaseDto) userService.amountChange(userId,secretString,md5);
        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(false,"操作成功!"));
        }
        return JSON.toJSONString(new Result(false,"操作失败!"));
    }

    //余额查询接口
    //余额通过流水记录而来
    @PostMapping("/balance")
    @ResponseBody
    public String amountChange(String userId ) throws BaseExceptionWithMessage, BaseException {
        if (userId==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        User u = new User(); u.setId(userId);
        SimpleBaseDto sbd = (SimpleBaseDto) userService.queryBalance(u);
        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(true,sbd.getTarget()));
        }
        return JSON.toJSONString(new Result(false,"操作失败!"));
    }

    //账单更新
    @PostMapping("/billstatupdate")
    @ResponseBody
    public String amountstatChange(String type,String id,String code) throws BaseExceptionWithMessage {
        if (type==null||id==null||code==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        SimpleBaseDto sbd = (SimpleBaseDto) userService.amountStatChange(type,id,code);
        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(false,"操作成功!"));
        }
        return JSON.toJSONString(new Result(false,"操作失败!"));
    }

}
