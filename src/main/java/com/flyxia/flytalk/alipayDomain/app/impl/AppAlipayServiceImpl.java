package com.flyxia.flytalk.alipayDomain.app.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransPayResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.flyxia.flytalk.alipayDomain.AlipayConstants;
import com.flyxia.flytalk.alipayDomain.app.AppAlipayService;
import com.flyxia.flytalk.alipayDomain.dto.Merchandise;
import com.flyxia.flytalk.dao.WithdrawBillDao;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.WithDrawBill;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/11 17:25
 */
@Service
public class AppAlipayServiceImpl implements AppAlipayService {
    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private WithdrawBillDao withdrawBillDao;

    @Override
    public BaseDto doPay(Merchandise merchandise) {
        AlipayTradeAppPayRequest appPayRequest = new AlipayTradeAppPayRequest();
        appPayRequest.setNotifyUrl(AlipayConstants.NOTIFY_URL);

        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        model.setBody(merchandise.getBody());
        model.setSubject(merchandise.getSubject());
        model.setOutTradeNo(merchandise.getOut_trade_no());
        model.setTimeoutExpress("30m");
        model.setTotalAmount(merchandise.getTotal_amount()+"");
        model.setProductCode("QUICK_MSECURITY_PAY");

        appPayRequest.setBizModel(model);
        String body="";
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(appPayRequest);
            System.out.println(body=response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return new SimpleBaseDto(body);
    }

    @Override
    public BaseDto doRefund(Map<String, String> params) throws AlipayApiException, BaseExceptionWithMessage {
        AlipayFundTransToaccountTransferRequest request =new AlipayFundTransToaccountTransferRequest();
        HashMap<String,String> contentMap=new HashMap<>();
        contentMap.put("out_biz_no",params.get("out_trade_no"));
        contentMap.put("payee_type",params.get("payee_type"));
        contentMap.put("payee_account",params.get("payee_account"));
        contentMap.put("amount",params.get("amount"));
        contentMap.put("payer_show_name",params.get("payer_show_name"));
        contentMap.put("payee_real_name",params.get("payee_real_name"));
        contentMap.put("remark",params.get("remark"));
        request.setBizContent(JSON.toJSONString(contentMap));

        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
        if (response.isSuccess()){
           String billId= response.getOutBizNo();
           WithDrawBill wb = withdrawBillDao.getOne(billId);
           if (wb==null) throw new BaseExceptionWithMessage("账单错误!");
           wb.setState("completed");
           withdrawBillDao.save(wb);
            return new SimpleBaseDto("退款成功!");
        }else {
            Map<String,String> param = new HashMap<>();
            param.put("out_biz_no",params.get("out_trade_no"));
           return doRefundQuery(param);
        }

    }

    @Override
    public BaseDto doRefundQuery(Map<String, String> params) throws AlipayApiException, BaseExceptionWithMessage {
        HashMap<String,String> contentMap=new HashMap<>();
        contentMap.put("out_biz_no",params.get("out_trade_no"));

        AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        request.setBizContent(JSON.toJSONString(contentMap));

        AlipayFundTransOrderQueryResponse response = alipayClient.execute(request);

        if (response.isSuccess()){
           String status= response.getStatus();
           if ("SUCCESS".equals(status)){
               return new SimpleBaseDto("转账成功!");
           }else {
               throw new BaseExceptionWithMessage("转账失败!");
           }
        }else {
            throw new BaseExceptionWithMessage("接口调用失败!");
        }
    }
}
