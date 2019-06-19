package com.flyxia.flytalk.constants;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author automannn@163.com
 * @time 2019/5/3 13:26
 */
public interface GlobleConfigConstants {
    Boolean TOP_UP_RETURN_OBJECT =false;

    Boolean WITHDRAW_RETURN_OBJECT =false;

    String TOPUP_TYPE ="topup";

    String WITH_DRAW_TYPE ="withdraw";

    String ALIPAY_TYPE="ALIPAY";

    String WEPAY_TYPE="WEPAY";

    String UNIPAY_TYPE="UNIONPAY";

    String PAY_CHANNEL_APP="APP";

    String PAY_CHANNEL_WAP="WAP";

    String PAY_CHANNEL_WEB="WEB";

    String PAY_CHANNEL_JSPI="JSPI";

    ReadWriteLock lock = new ReentrantReadWriteLock();

//    String DOMAIN="https://wechat.automannn.cn";
    String DOMAIN="http://h82dyr.natappfree.cc";


}
