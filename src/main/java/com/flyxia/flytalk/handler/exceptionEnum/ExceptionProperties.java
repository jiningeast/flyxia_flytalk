package com.flyxia.flytalk.handler.exceptionEnum;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/4/26 9:29
 */
@ConfigurationProperties(prefix = "com.flyxia.exception")
public class ExceptionProperties {

    private Map<Integer,String> exceptionMaps = new HashMap<Integer, String>();

    public Map<Integer, String> getExceptionMaps() {
        return exceptionMaps;
    }

    public void setExceptionMaps(Map<Integer, String> exceptionMaps) {
        this.exceptionMaps = exceptionMaps;
    }
}
