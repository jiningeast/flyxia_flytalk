package com.flyxia.flytalk.alipayDomain;

import com.alipay.api.AlipayApiException;
import com.flyxia.flytalk.alipayDomain.dto.Merchandise;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;

import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/10 16:15
 */
public interface AlipayService {
    BaseDto doPay(Merchandise merchandise);

    BaseDto doRefund(Map<String, String> params) throws AlipayApiException, BaseExceptionWithMessage;

    BaseDto doRefundQuery(Map<String, String> params) throws AlipayApiException, BaseExceptionWithMessage;
}
