package com.flyxia.flytalk.security.autoConfig;

import com.flyxia.flytalk.security.sender.DefaultSmsCodeSender;
import com.flyxia.flytalk.security.sender.SmsCodeSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author automannn@163.com
 * @time 2019/4/27 16:47
 */
@Configuration
public class ValidateCodeBeanConfig {

    @Bean
    @ConditionalOnMissingBean(SmsCodeSender.class)
    public SmsCodeSender smsCodeSender(){
        return new DefaultSmsCodeSender();
    }
}
