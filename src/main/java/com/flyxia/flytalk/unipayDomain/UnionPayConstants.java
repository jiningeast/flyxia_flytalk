package com.flyxia.flytalk.unipayDomain;

import com.flyxia.flytalk.constants.GlobleConfigConstants;

/**
 * @author automannn@163.com
 * @time 2019/5/23 9:17
 */
public interface UnionPayConstants {

   // String MERCHAT_ID="777290058110048";
    String MERCHAT_ID="777290058110097";

    //签名证书
    String SIGN_CERT="";

    //签名证书密码  测试环境为： 000000
    String SIGN_CERT_PWD="";

    //签名证书类型
    String SIGN_CERT_TYPE="PKCS12";

    //敏感信息加密证书
    String SENSITIVE_INFO_ENCRIPT_CERT="";

    //验签 中 级 证书
    String MIDDLE_VERIFY_CERT="";

    //验签 根 证书
    String ROOT_VERIFY_CERT="";


    //后台异步通知地址
    String BACK_URL=GlobleConfigConstants.DOMAIN+"/unionpay/notify";

    //前台通知地址
    String FRONT_URL=GlobleConfigConstants.DOMAIN+"/test/hi";

    //前台交易地址
    String FRONT_TRANS_URL = "";


    //单账单查询地址
    String SINGLE_QUERY_URL ="";

    //文件传输地址
    String FILE_TRANS_URL="";

    //app交易地址
    String APP_TRANS_URL="";

    String CARD_TRANS_URL="";

    //报文版本号
    String VERSION="5.1.0";

    //01 表示rsa签名方式
    String SIGN_MODE="01";

    //是否验证 验签证书的 cn
    Boolean IF_VALIDATE_CN_NAME=false;

    //是否验证 https证数
    Boolean IF_VALIDATE_REMOTE_CERT=false;

    String ENCODING="utf-8";


}
