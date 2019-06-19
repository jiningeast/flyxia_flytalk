package com.flyxia.flytalk.security.processor;

import com.flyxia.flytalk.constants.SecurityConstants;
import com.flyxia.flytalk.security.sender.SmsCodeSender;
import com.flyxia.flytalk.validate.ValidateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author automannn@163.com
 * @time 2019/4/27 15:14
 */
//注意，getSimple 得到的是类名；  而 smsValidateCodeProcessor是容器中的bean名
@Component("smsValidateCodeProcessor")
public class SmsCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode> {
    //短信验证码发送器
    @Autowired
    private SmsCodeSender smsCodeSender;

    @Override
    protected void send(ServletWebRequest request, ValidateCode validateCode) throws Exception {
        String paramName = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;
        //获取手机号
        String mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(),paramName);
        smsCodeSender.send(mobile,validateCode.getCode());
    }
}
