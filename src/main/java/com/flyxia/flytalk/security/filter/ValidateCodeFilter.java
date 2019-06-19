package com.flyxia.flytalk.security.filter;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.constants.SecurityConstants;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exceptionEnum.ExceptionEnum;
import com.flyxia.flytalk.handler.exceptionEnum.ExceptionProperties;
import com.flyxia.flytalk.security.SecurityProperties;
import com.flyxia.flytalk.security.exception.ValidateAuthenticationException;
import com.flyxia.flytalk.security.processor.ValidateCodeProcessorHolder;
import com.flyxia.flytalk.security.processor.ValidateCodeType;
import com.flyxia.flytalk.vo.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author automannn@163.com
 * @time 2019/4/27 15:38
 */

@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {
    //验证码校验失败处理器
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    //系统配置信息
    @Autowired
    private SecurityProperties securityProperties;

    //注入系统中的校验码处理器
    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    //用于细化失败原因
    @Autowired
    private ExceptionProperties exceptionProperties;

    //存放所有需要校验验证码的url
    private Map<String,ValidateCodeType> urlMap = new HashMap<>();

    //验证请求url 与配置的url 是否匹配的工具类
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    //责任链模式
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        ValidateCodeType type = getValidateCodeType(httpServletRequest);
        if (type!=null){
            try {
                validateCodeProcessorHolder.findValidateCodeProcessor(type).validate(new ServletWebRequest(httpServletRequest,httpServletResponse));
            } catch (BaseException e) {
                int code = e.getErrorCode();
                Map<Integer,String> maps = exceptionProperties.getExceptionMaps();
                String message= maps.get(code);
                if (message!=null&&!"".equals(message.trim())){
                    authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, new ValidateAuthenticationException(message));
                }else {
                    authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, new ValidateAuthenticationException("验证码验证失败！"));
                }
                return;
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }

    //获取校验码的类型，如果当前请求不需要校验，就返回null
    private ValidateCodeType getValidateCodeType(HttpServletRequest request){
        ValidateCodeType result = null;
        if (!StringUtils.equalsIgnoreCase(request.getMethod(),"get")){
            Set<String> urls = urlMap.keySet();
            for (String url: urls){
                if (pathMatcher.match(url,request.getRequestURI())){
                    result = urlMap.get(url);
                }
            }
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        //urlMap.put(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,ValidateCodeType.SMS);
        urlMap.put(SecurityConstants.DEFAULT_VALIDATE_CODE_MOBILE_VALIDATE,ValidateCodeType.SMS);
        addUrlToMap(securityProperties.getCode().getSms().getUrl(),ValidateCodeType.SMS);
    }

    //将系统配置的需要校验验证码的URL根据校验的类型放入map
    protected  void addUrlToMap(String urlString,ValidateCodeType type){
        if (StringUtils.isBlank(urlString)){
            String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString,",");
            if(urls!=null)
                for (String url: urls){
                    urlMap.put(url,type);
                }
        }
    }
}
