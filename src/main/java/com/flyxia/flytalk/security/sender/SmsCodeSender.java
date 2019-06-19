package com.flyxia.flytalk.security.sender;

/**
 * @author automannn@163.com
 * @time 2019/4/27 15:15
 */
public interface SmsCodeSender {
    void send(String mobile, String code) throws Exception;
}
