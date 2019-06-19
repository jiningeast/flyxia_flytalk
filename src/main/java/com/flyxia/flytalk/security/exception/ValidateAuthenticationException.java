package com.flyxia.flytalk.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author automannn@163.com
 * @time 2019/4/27 16:01
 */
public class ValidateAuthenticationException extends AuthenticationException{
    public ValidateAuthenticationException(String msg) {
        super(msg);
    }
}
