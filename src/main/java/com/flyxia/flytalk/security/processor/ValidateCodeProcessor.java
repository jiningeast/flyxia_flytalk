package com.flyxia.flytalk.security.processor;

import com.flyxia.flytalk.handler.exception.BaseException;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author automannn@163.com
 * @time 2019/4/27 13:49
 */
public interface ValidateCodeProcessor {

    //验证码放入session时的前缀
    String SESSION_KEY_PREFIX ="SESSION_KEY_FOR_CODE_";

    //创建校验码
    void create(ServletWebRequest request) throws  Exception;

    //校验验证码
    void validate(ServletWebRequest request) throws BaseException;
}
