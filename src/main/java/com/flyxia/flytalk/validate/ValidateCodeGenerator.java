package com.flyxia.flytalk.validate;

import org.springframework.web.context.request.ServletWebRequest;


/**
 * @author automannn@163.com
 * @time 2019/4/27 10:41
 */
public interface ValidateCodeGenerator {
    ValidateCode generate(ServletWebRequest request);
}
