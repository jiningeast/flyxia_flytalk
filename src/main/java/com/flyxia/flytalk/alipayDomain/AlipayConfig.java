package com.flyxia.flytalk.alipayDomain;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.flyxia.flytalk.alipayDomain.wap.impl.WapAlipayServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author automannn@163.com
 * @time 2019/5/10 13:46
 */
@Configuration
public class AlipayConfig {
    @Bean
    public AlipayClient alipayClient(){
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConstants.GATEWAY_URL,
                AlipayConstants.APP_ID,AlipayConstants.MERCHAT_PRI_KEY,AlipayConstants.FORMAT,
                AlipayConstants.CHARSET_TYPE,AlipayConstants.ALIPAY_PUB_KEY,AlipayConstants.SIGN_TYPE);

        return alipayClient;
    }

    @ConditionalOnMissingBean(AlipayService.class)
    @Bean
    public AlipayService alipayService(){
        WapAlipayServiceImpl wapAlipayService =new WapAlipayServiceImpl();
        return  wapAlipayService;
    }
}
