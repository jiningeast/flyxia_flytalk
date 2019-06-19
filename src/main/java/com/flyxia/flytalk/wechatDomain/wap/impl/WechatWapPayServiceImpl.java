package com.flyxia.flytalk.wechatDomain.wap.impl;

import com.automannn.commonUtils.http.Https;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.wechatDomain.wap.WechatWapPayService;

import javax.servlet.http.HttpServletResponse;

/**
 * @author automannn@163.com
 * @time 2019/5/13 17:27
 */
public class WechatWapPayServiceImpl implements WechatWapPayService {
    @Override
    public BaseDto requestUniPayApi(String xmlparam) {

        //todo: h5支付  需要额外加入一些参数；
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
