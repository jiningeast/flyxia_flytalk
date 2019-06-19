package com.flyxia.flytalk.alipayDomain.withback;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author automannn@163.com
 * @time 2019/5/20 13:50
 */
@RestController
@RequestMapping("/alipay")
public class AlipayWithdraw {

    @Autowired
    private AlipayClient alipayClient;

    //退款请求
    @PostMapping("/withdraw")
    public String withdraw(HashMap<String,String> params) throws BaseExceptionWithMessage, AlipayApiException {
        if (params.get("userId")==null||params.get("out_biz_no")==null||params.get("payee_type")==null||
                params.get("payee_account")==null||params.get("amount")==null||params.get("payer_show_name")==null||
                params.get("payee_real_name")==null||params.get("remark")==null){
            throw new BaseExceptionWithMessage("参数错误");
        }

        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();

        request.setBizContent(JSON.toJSONString(params));

        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);

        if (response.isSuccess()){
            System.out.println("调用成功!");
            return JSON.toJSONString(new Result(true,"退款成功!"));
        }else {
            System.out.println("调用失败!");
            return JSON.toJSONString(new Result(false,"退款申请已提交!"));
        }
    }

    //退款进度查询
    @PostMapping("/withdraw_query")
    public String withdrawQuery(HashMap<String,String> params) throws BaseExceptionWithMessage, AlipayApiException {
        if (params.get("userId")==null||params.get("out_biz_no")==null||params.get("order_id")==null){
            throw new BaseExceptionWithMessage("参数错误");
        }

        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();

        request.setBizContent(JSON.toJSONString(params));

        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);

        if (response.isSuccess()){
            System.out.println("查询成功!");
            return JSON.toJSONString(new Result(true,"退款结果查询成功!"));
        }else {
            System.out.println("查询失败!");
            return JSON.toJSONString(new Result(false,"退款结果查询失败"));
        }
    }
}
