package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author automannn@163.com
 * @time 2019/4/25 13:37
 */
public interface UserService {
    //注册
    BaseDto logOn(User u) throws BaseException;

    //查询用户信息
    BaseDto query(User u) throws BaseException;

    //查询余额
    BaseDto queryBalance(User u) throws BaseException;

    //查询用户信息
    BaseDto login(User u) throws BaseException;

    //查询所有用户信息
    BaseDto queryAll();

    BaseDto amountChange(String userId, String secretString, String md5) throws BaseExceptionWithMessage;

    BaseDto amountStatChange(String type, String id, String code) throws BaseExceptionWithMessage;

    BaseDto setAvatar(MultipartFile file, String userId) throws Exception;




}
