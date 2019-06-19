package com.flyxia.flytalk.validate.sms;

import com.flyxia.flytalk.security.SecurityProperties;
import com.flyxia.flytalk.validate.ValidateCode;
import com.flyxia.flytalk.validate.ValidateCodeGenerator;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletRequest;

/**
 * @author automannn@163.com
 * @time 2019/4/27 10:40
 */
@Component("smsValidateCodeGenerator")
public class SmsCodeGenetor implements ValidateCodeGenerator{
    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public ValidateCode generate(ServletWebRequest request) {
        String code = RandomStringUtils.randomNumeric(securityProperties.getCode().getSms().getLength());
        return new ValidateCode(code,securityProperties.getCode().getSms().getExpireIn());
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

}
