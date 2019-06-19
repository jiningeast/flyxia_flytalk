package com.flyxia.flytalk.wechatDomain.web.impl;

import com.automannn.commonUtils.http.Https;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.wechatDomain.web.WechatWebPayService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author automannn@163.com
 * @time 2019/5/17 9:47
 */

/*
*   模式一带有入参
*   模式二则是根据具体的业务逻辑展开就行了
*   他们区别是(入参中是否包含： openid,mch_id等信息(针对 controller))
* */
public class WechatWebPayServiceImpl implements WechatWebPayService {
    @Override
    public BaseDto requestUniPayApi(String xmlparam) {
        final String[] s = {null};
        Thread t = new Thread(()->{
            s[0] = Https.post(UNIQUE_PAY_API).body(xmlparam).request();
        });

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (s[0]!=null){
            return new SimpleBaseDto(s[0]);
        }
        return new SimpleBaseDto(null);
    }

    @Override
    public BaseDto refund2Account(String xmlparam) {
        return null;
    }

    @Override
    public BaseDto refund2Bank(String xmlparam) {
        return null;
    }

    @Override
    public BaseDto refund2BankQuery(String xmlparam) {
        return null;
    }

    @Override
    public BaseDto refund2AccountQuery(String xmlparam) {
        return null;
    }

    @Override
    public BaseDto getpubkey(String xmlparam) {
        return null;
    }
}
