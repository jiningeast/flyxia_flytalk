package com.flyxia.flytalk.wechatDomain;

import com.flyxia.flytalk.dto.BaseDto;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author automannn@163.com
 * @time 2019/5/13 17:21
 */
public interface WechatPayService {
    String UNIQUE_PAY_API="https://api.mch.weixin.qq.com/pay/unifiedorder";

    String UNIQUE_ACCOUNT_REFUND_API="https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    String UNIQUE_BANK_REFUND_API="https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";

    String UNIQUE_REFUND_ACCOUNT_QUERY_API = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    String UNIQUE_REFUND_BANK_QUERY_API = "https://api.mch.weixin.qq.com/mmpaysptrans/query_bank";

    String UNIQUE_GETPUBKEY_API="https://fraud.mch.weixin.qq.com/risk/getpublickey";

    //请求统一支付接口--同步方法
    BaseDto requestUniPayApi(String xmlparam);
    //请求退款到账户接口--同步方法
    BaseDto refund2Account(String xmlparam);

    BaseDto refund2Bank(String xmlparam);

    BaseDto refund2BankQuery(String xmlparam);

    BaseDto refund2AccountQuery(String xmlparam);

    BaseDto getpubkey(String xmlparam);
}
