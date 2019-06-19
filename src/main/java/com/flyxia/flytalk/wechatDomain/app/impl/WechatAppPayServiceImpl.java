package com.flyxia.flytalk.wechatDomain.app.impl;

import com.automannn.commonUtils.http.Https;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.util.XMLutil;
import com.flyxia.flytalk.wechatDomain.WechatPayConstants;
import com.flyxia.flytalk.wechatDomain.app.WechatAppPayService;
import com.flyxia.flytalk.wechatDomain.util.SignUtil;
import org.jdom.JDOMException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author automannn@163.com
 * @time 2019/5/13 17:41
 */
@Service
public class WechatAppPayServiceImpl implements WechatAppPayService {

    @Override
    public  BaseDto requestUniPayApi(String xmlparam) {
        return doLogic(xmlparam,UNIQUE_PAY_API);
    }

    @Override
    public BaseDto refund2Account(String xmlparam) {
        return doLogic(xmlparam,UNIQUE_ACCOUNT_REFUND_API);
    }

    @Override
    public BaseDto refund2Bank(String xmlparam) {
        return doLogic(xmlparam,UNIQUE_BANK_REFUND_API);
    }

    @Override
    public BaseDto refund2BankQuery(String xmlparam) {
        return doLogic(xmlparam,UNIQUE_REFUND_BANK_QUERY_API);
    }

    @Override
    public BaseDto refund2AccountQuery(String xmlparam) {
        return doLogic(xmlparam, UNIQUE_REFUND_ACCOUNT_QUERY_API);
    }

    @Override
    public BaseDto getpubkey(String xmlparam) {
        return doLogic(xmlparam,UNIQUE_GETPUBKEY_API);
    }

    private SimpleBaseDto doLogic(String xmlparam,String api){
        final String[] s = new String[1];

        Thread t = new Thread(() -> {
            s[0]= Https.post(api).body(xmlparam).request();
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (s[0] ==null) {
            SimpleBaseDto baseDto = new SimpleBaseDto("空");
            return baseDto;
        } else {
            Map params=null;
            try {
                params =  XMLutil.doXMLParse(s[0]);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //加密
            HashMap kvMap = new HashMap();
            kvMap.put("appid",params.get("appid"));
            kvMap.put("partnerid",params.get("mch_id"));
            kvMap.put("prepayid",params.get("prepay_id"));
            kvMap.put("noncestr",params.get("nonce_str"));
            kvMap.put("timestamp",new Date().getTime()+"");
            kvMap.put("package","Sign=WXPay");

            SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);
            kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));

            SimpleBaseDto baseDto = new SimpleBaseDto(kvMap);
            return baseDto;
        }
    }
}

