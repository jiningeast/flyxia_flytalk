package com.flyxia.flytalk.security.sender;



/**
 * @author automannn@163.com
 * @time 2019/4/27 15:16
 */

public class DefaultSmsCodeSender implements SmsCodeSender {
    @Override
    public void send(String mobile, String code) {
        //todo: 调用短信平台发送信息
        System.out.println("向手机："+mobile+"发送了验证码"+code);
    }
}
