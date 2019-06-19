package com.flyxia.flytalk.security.processor;

import com.flyxia.flytalk.constants.SecurityConstants;

/**
 * @author automannn@163.com
 * @time 2019/4/27 14:12
 */
public enum ValidateCodeType {
    SMS{
        @Override
        public String getParamNameOnValidate() {
            return SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_SMS;
        }
    };

    public abstract String getParamNameOnValidate();
}
