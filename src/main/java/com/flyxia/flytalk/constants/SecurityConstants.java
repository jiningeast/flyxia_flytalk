package com.flyxia.flytalk.constants;

/**
 * @author automannn@163.com
 * @time 2019/4/27 13:42
 */
public interface SecurityConstants {

    //默认的处理验证码的url前缀
    String DEFAULT_VALIDATE_CODE_URL_PREFIX="/code";

    //默认的 手机验证码验证路径
    String DEFAULT_VALIDATE_CODE_MOBILE_VALIDATE="/validate/mobile";

    //默认的手机验证码登陆请求处理 url
    String DEFAULT_LOGIN_PROCESSING_URL_MOBILE="/authentication/mobile";

    //发送短信验证码，及 验证短信验证码时，传递手机号的参数的名称
    String DEFAULT_PARAMETER_NAME_MOBILE="phoneNumber";

    //验证短信验证码时，请求中携带短信验证码信息的默认参数名称
    String DEFAULT_PARAMETER_NAME_CODE_SMS="smsCode";
}
