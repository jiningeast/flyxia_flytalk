package com.flyxia.flytalk.wechatDomain;

import com.flyxia.flytalk.constants.GlobleConfigConstants;

/**
 * @author automannn@163.com
 * @time 2019/5/13 10:07
 */
public interface WechatPayConstants {
    String APP_ID="wx440625ae89c4244d";

    String MCH_ID="1536038991";

    String PRI_KEY="c91fdc31a8370c9e3642410a16433d27";

    String NOTIFY_URL =GlobleConfigConstants.DOMAIN+"/wechatpay/notify";

    //商户号的 appid ,用于退款
    String MCH_APPID="";

}
