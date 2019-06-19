package com.flyxia.flytalk.alipayDomain.wap.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.flyxia.flytalk.alipayDomain.AlipayConstants;
import com.flyxia.flytalk.alipayDomain.dto.Merchandise;
import com.flyxia.flytalk.alipayDomain.wap.WapAlipayService;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/10 14:41
 */
public class WapAlipayServiceImpl implements WapAlipayService {
    @Autowired
    private AlipayClient alipayClient;

    @Override
    public BaseDto doPay(Merchandise merchandise) {
        AlipayTradeWapPayRequest wapPayRequest = new AlipayTradeWapPayRequest();

        wapPayRequest.setReturnUrl(AlipayConstants.RETURN_URL);
        wapPayRequest.setNotifyUrl(AlipayConstants.NOTIFY_URL);

        String bizString = JSON.toJSONString(merchandise);

        wapPayRequest.setBizContent(bizString);

        String form = "";
        try {
            form = alipayClient.pageExecute(wapPayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return new SimpleBaseDto(form);
    }

    @Override
    public BaseDto doRefund(Map<String, String> params) {
        return null;
    }

    @Override
    public BaseDto doRefundQuery(Map<String, String> params) throws AlipayApiException, BaseExceptionWithMessage {
        return null;
    }
}
