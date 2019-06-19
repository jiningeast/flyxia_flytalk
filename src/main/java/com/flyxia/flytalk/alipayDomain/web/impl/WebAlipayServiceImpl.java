package com.flyxia.flytalk.alipayDomain.web.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.flyxia.flytalk.alipayDomain.AlipayConstants;
import com.flyxia.flytalk.alipayDomain.dto.Merchandise;
import com.flyxia.flytalk.alipayDomain.web.WebAlipayService;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/10 14:31
 */

public class WebAlipayServiceImpl implements WebAlipayService {
    @Autowired
    private AlipayClient alipayClient;

    @Override
    public BaseDto doPay(Merchandise merchandise) {
        AlipayTradePagePayRequest pagePayRequest = new AlipayTradePagePayRequest();

        pagePayRequest.setReturnUrl(AlipayConstants.RETURN_URL);
        pagePayRequest.setNotifyUrl(AlipayConstants.NOTIFY_URL);

        String bizString = JSON.toJSONString(merchandise);

        pagePayRequest.setBizContent(bizString);

        String form ="";
        try {
            form = alipayClient.pageExecute(pagePayRequest).getBody();
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

    //退款接口
    public void doRefund(){};
    //交易退款查询接口
    public void doRefundQuery(){};
    //收单线下交易查询接口
    public void doTradeQuery(){};
    //收单交易关闭接口
    public void doTradeClose(){};
    //查询对账单下载地址
    public void doDownloadUrlQuery(){};


}
