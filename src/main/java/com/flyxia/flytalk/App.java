package com.flyxia.flytalk;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

/**
 * @author automannn@163.com
 * @time 2019/4/25 11:18
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    //让http重定向到 https
    @Bean
    @ConditionalOnProperty(prefix="com.automannn",name = "https", havingValue = "true")
    public TomcatEmbeddedServletContainerFactory servletContainerFactory(){
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory(){
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint =new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);

                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return  tomcat;
    }

    @Bean
    @ConditionalOnProperty(prefix="com.automannn",name = "https", havingValue = "true")
    public Connector httpConnector(){
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");

        //connector监听的端口号
        connector.setPort(8060);
        connector.setSecure(false);

        //监听到http的端口号后，转向到的https的端口号
        connector.setRedirectPort(443);
        return connector;

    }
}
