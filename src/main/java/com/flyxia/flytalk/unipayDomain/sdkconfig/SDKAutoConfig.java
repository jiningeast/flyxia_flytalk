package com.flyxia.flytalk.unipayDomain.sdkconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author automannn@163.com
 * @time 2019/5/23 13:39
 */
@Configuration
public class SDKAutoConfig {

    @Bean
    public UnionPaySDKConfig sdkConfig(){
        
        //一方面，设置静态代理
        //一方面，将该静态代理装配到容器里面
        UnionPaySDKConfig unionPaySdkConfig = UnionPaySDKConfig.getConfig();
        unionPaySdkConfig.loadPropertiesFromSrc();
        return unionPaySdkConfig;
    }
}
