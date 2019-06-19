package com.flyxia.flytalk.security.processor;

import com.flyxia.flytalk.handler.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/4/27 13:48
 */
@Component
public class ValidateCodeProcessorHolder {
    @Autowired
    private Map<String,ValidateCodeProcessor> validateCodeProcessors;

    public ValidateCodeProcessor findValidateCodeProcessor(ValidateCodeType type) throws BaseException {
        return findValidateCodeProcessor(type.toString().toLowerCase());
    }

    public ValidateCodeProcessor findValidateCodeProcessor(String type) throws BaseException {
        //获取验证码类型
        String name = type.toLowerCase()+ValidateCodeProcessor.class.getSimpleName();

        //在集合中找到该类型验证码的处理程序
        ValidateCodeProcessor processor = validateCodeProcessors.get(name);

        if (processor == null) throw new BaseException(2000);
        return  processor;
    }
}
