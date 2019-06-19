package com.flyxia.flytalk.validate;

import com.flyxia.flytalk.validate.sms.properties.SmsCodeProperties;

/**
 * @author automannn@163.com
 * @time 2019/4/27 13:27
 */
//这里可以添加其他类型的验证。 如：图片验证码
public class ValidateCodeProperties {
    private SmsCodeProperties sms = new SmsCodeProperties();

    public SmsCodeProperties getSms() {
        return sms;
    }

    public void setSms(SmsCodeProperties sms) {
        this.sms = sms;
    }
}
