package com.flyxia.flytalk.security.processor;

import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.validate.ValidateCode;
import com.flyxia.flytalk.validate.ValidateCodeGenerator;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/4/27 14:21
 */

//模板模式，封装了不变的操作
public abstract class AbstractValidateCodeProcessor<C extends ValidateCode> implements ValidateCodeProcessor {
    //操作session的工具类
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    //收集系统中所有 validatCodeGenerator 接口的实现
    @Autowired
    private Map<String,ValidateCodeGenerator> validateCodeGenerators;

    //核心流程： 1.生成验证码  2.将验证码 与session信息保存起来   3.发送给客户端
    @Override
    public void create(ServletWebRequest request) throws Exception {
        C validateCode =generate(request);
        save(request,validateCode);
        send(request,validateCode);
    }

    private void save(ServletWebRequest request,C validateCode){
        sessionStrategy.setAttribute(request,getSessionKey(request),validateCode);
    }

    //构建验证码放入 session 时的key
    private String getSessionKey(ServletWebRequest request){
        return  SESSION_KEY_PREFIX +getValidateCodeType(request).toString().toUpperCase();
    }

    //发送校验码，由子类实现
   protected abstract void send(ServletWebRequest request,C validateCode) throws Exception;

    //生成校验码
    private C generate(ServletWebRequest request) throws BaseException {
        String type = getValidateCodeType(request).toString().toLowerCase();

        //生成器的名称
        String generatorName = type + ValidateCodeGenerator.class.getSimpleName();
        //从集合中取得该生成器
        ValidateCodeGenerator validateCodeGenerator = validateCodeGenerators.get(generatorName);
        if (validateCodeGenerator ==null) throw new BaseException(2001);
        return (C) validateCodeGenerator.generate(request);
    }

    //根据请求的url 获取校验码的类型(注意，这里的逻辑没有再从 request中取了,而是从当前处理器。  当前处理器 由 holdler进行选取)
    private ValidateCodeType getValidateCodeType(ServletWebRequest request){
        String type = StringUtils.substringBefore(getClass().getSimpleName(),"CodeProcessor");
        return ValidateCodeType.valueOf(type.toUpperCase());
    }

    //验证的流程 (核心)
    @Override
    public void validate(ServletWebRequest request) throws BaseException {
        ValidateCodeType processorType = getValidateCodeType(request);
        String sessionKey = getSessionKey(request);

        C codeInSession = (C) sessionStrategy.getAttribute(request,sessionKey);

        String codeInRequest;
        try {
            codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),processorType.getParamNameOnValidate());
        } catch (ServletRequestBindingException e) {
            throw new BaseException(3000);
        }

        if (StringUtils.isBlank(codeInRequest)) throw new BaseException(3001);
        if (codeInSession==null) throw new BaseException(3002);
        if (codeInSession.isExpried()){
            sessionStrategy.removeAttribute(request,sessionKey);
            throw new BaseException(3003);
        }

        if (!StringUtils.equals(codeInSession.getCode(),codeInRequest)) throw new BaseException(3004);

        sessionStrategy.removeAttribute(request,sessionKey);
    }
}
