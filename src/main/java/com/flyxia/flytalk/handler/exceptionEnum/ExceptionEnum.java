package com.flyxia.flytalk.handler.exceptionEnum;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author automannn@163.com
 * @time 2019/4/26 9:21
 */

public class ExceptionEnum {


    public int code;
    public String message;

    public ExceptionEnum(int code ,String message){
        this.code=code;
        this.message = message;
    }

    public ExceptionEnum() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
