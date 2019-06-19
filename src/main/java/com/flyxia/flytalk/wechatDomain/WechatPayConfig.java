package com.flyxia.flytalk.wechatDomain;

import com.flyxia.flytalk.alipayDomain.wap.impl.WapAlipayServiceImpl;
import com.flyxia.flytalk.wechatDomain.wap.WechatWapPayService;
import com.flyxia.flytalk.wechatDomain.wap.impl.WechatWapPayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author automannn@163.com
 * @time 2019/5/13 17:24
 */
@Configuration
public class WechatPayConfig {

    @ConditionalOnMissingBean(WechatPayService.class)
    @Bean
    public WechatPayService wechatPayService(){
        WechatWapPayServiceImpl wechatWapPayService = new WechatWapPayServiceImpl();
        return  wechatWapPayService;
    }

}
