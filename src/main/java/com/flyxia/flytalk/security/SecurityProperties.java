package com.flyxia.flytalk.security;

import com.flyxia.flytalk.validate.ValidateCodeProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author automannn@163.com
 * @time 2019/4/27 11:57
 */
@ConfigurationProperties(prefix = "com.flyxia.security")
public class SecurityProperties {
    ValidateCodeProperties code = new ValidateCodeProperties();

    public ValidateCodeProperties getCode() {
        return code;
    }

    public void setCode(ValidateCodeProperties code) {
        this.code = code;
    }
}
