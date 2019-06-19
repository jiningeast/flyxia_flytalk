package com.flyxia.flytalk.handler.exception;

/**
 * @author automannn@163.com
 * @time 2019/4/26 9:20
 */
public class BaseException extends Exception{
    private int errorCode ;

    public BaseException(int errorCode){this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
