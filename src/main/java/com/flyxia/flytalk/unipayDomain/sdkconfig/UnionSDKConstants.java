package com.flyxia.flytalk.unipayDomain.sdkconfig;

/**
 * @author automannn@163.com
 * @time 2019/5/23 10:16
 */
public interface UnionSDKConstants {

    String COLUMN_DEFAULT = "-";

    String KEY_DELIMITER = "#";

    /** memeber variable: blank. */
    String BLANK = "";

    /** member variabel: space. */
    String SPACE = " ";

    /** memeber variable: unline. */
    String UNLINE = "_";

    /** memeber varibale: star. */
    String STAR = "*";

    /** memeber variable: line. */
    String LINE = "-";

    /** memeber variable: add. */
    String ADD = "+";

    /** memeber variable: colon. */
    String COLON = "|";

    /** memeber variable: point. */
    String POINT = ".";

    /** memeber variable: comma. */
    String COMMA = ",";

    /** memeber variable: slash. */
    String SLASH = "/";

    /** memeber variable: div. */
    String DIV = "/";

    /** memeber variable: left . */
    String LB = "(";

    /** memeber variable: right. */
    String RB = ")";

    /** memeber variable: rmb. */
    String CUR_RMB = "RMB";

    /** memeber variable: .page size */
    int PAGE_SIZE = 10;

    /** memeber variable: String ONE. */
    String ONE = "1";

    /** memeber variable: String ZERO. */
    String ZERO = "0";

    /** memeber variable: number six. */
    int NUM_SIX = 6;

    /** memeber variable: equal mark. */
    String EQUAL = "=";

    /** memeber variable: operation ne. */
    String NE = "!=";

    /** memeber variable: operation le. */
    String LE = "<=";

    /** memeber variable: operation ge. */
    String GE = ">=";

    /** memeber variable: operation lt. */
    String LT = "<";

    /** memeber variable: operation gt. */
    String GT = ">";

    /** memeber variable: list separator. */
    String SEP = "./";

    /** memeber variable: Y. */
    String Y = "Y";

    /** memeber variable: AMPERSAND. */
    String AMPERSAND = "&";

    /** memeber variable: SQL_LIKE_TAG. */
    String SQL_LIKE_TAG = "%";

    /** memeber variable: @. */
    String MAIL = "@";

    /** memeber variable: number zero. */
    int NZERO = 0;

    String LEFT_BRACE = "{";

    String RIGHT_BRACE = "}";

    /** memeber variable: string true. */
    String TRUE_STRING = "true";
    /** memeber variable: string false. */
    String FALSE_STRING = "false";

    /** memeber variable: forward success. */
    String SUCCESS = "success";
    /** memeber variable: forward fail. */
    String FAIL = "fail";
    /** memeber variable: global forward success. */
    String GLOBAL_SUCCESS = "$success";
    /** memeber variable: global forward fail. */
    String GLOBAL_FAIL = "$fail";

    String UTF_8_ENCODING = "UTF-8";
    String GBK_ENCODING = "GBK";
    String CONTENT_TYPE = "Content-type";
    String APP_XML_TYPE = "application/xml;charset=utf-8";
    String APP_FORM_TYPE = "application/x-www-form-urlencoded;charset=";

    String VERSION_1_0_0 = "1.0.0";
    String VERSION_5_0_0 = "5.0.0";
    String VERSION_5_0_1 = "5.0.1";
    String VERSION_5_1_0 = "5.1.0";
    String SIGNMETHOD_RSA = "01";
    String SIGNMETHOD_SHA256 = "11";
    String SIGNMETHOD_SM3 = "12";
    String UNIONPAY_CNNAME = "中国银联股份有限公司";
    String CERTTYPE_01 = "01";// 敏感信息加密公钥
    String CERTTYPE_02 = "02";// 磁道加密公钥

    /******************************************** 5.0报文接口定义 ********************************************/
    /** 版本号. */
    String param_version = "version";
    /** 证书ID. */
    String param_certId = "certId";
    /** 签名. */
    String param_signature = "signature";
    /** 签名方法. */
    String param_signMethod = "signMethod";
    /** 编码方式. */
    String param_encoding = "encoding";
    /** 交易类型. */
    String param_txnType = "txnType";
    /** 交易子类. */
    String param_txnSubType = "txnSubType";
    /** 业务类型. */
   String param_bizType = "bizType";
    /** 前台通知地址 . */
    String param_frontUrl = "frontUrl";
    /** 后台通知地址. */
    String param_backUrl = "backUrl";
    /** 接入类型. */
    String param_accessType = "accessType";
    /** 收单机构代码. */
    String param_acqInsCode = "acqInsCode";
    /** 商户类别. */
    String param_merCatCode = "merCatCode";
    /** 商户类型. */
    String param_merType = "merType";
    /** 商户代码. */
    String param_merId = "merId";
    /** 商户名称. */
    String param_merName = "merName";
    /** 商户简称. */
    String param_merAbbr = "merAbbr";
    /** 二级商户代码. */
     String param_subMerId = "subMerId";
    /** 二级商户名称. */
     String param_subMerName = "subMerName";
    /** 二级商户简称. */
    String param_subMerAbbr = "subMerAbbr";
    /** Cupsecure 商户代码. */
    String param_csMerId = "csMerId";
    /** 商户订单号. */
    String param_orderId = "orderId";
    /** 交易时间. */
    String param_txnTime = "txnTime";
    /** 发送时间. */
    String param_txnSendTime = "txnSendTime";
    /** 订单超时时间间隔. */
    String param_orderTimeoutInterval = "orderTimeoutInterval";
    /** 支付超时时间. */
    String param_payTimeoutTime = "payTimeoutTime";
    /** 默认支付方式. */
    String param_defaultPayType = "defaultPayType";
    /** 支持支付方式. */
    String param_supPayType = "supPayType";
    /** 支付方式. */
    String param_payType = "payType";
    /** 自定义支付方式. */
    String param_customPayType = "customPayType";
    /** 物流标识. */
    String param_shippingFlag = "shippingFlag";
    /** 收货地址-国家. */
    String param_shippingCountryCode = "shippingCountryCode";
    /** 收货地址-省. */
    String param_shippingProvinceCode = "shippingProvinceCode";
    /** 收货地址-市. */
    String param_shippingCityCode = "shippingCityCode";
    /** 收货地址-地区. */
    String param_shippingDistrictCode = "shippingDistrictCode";
    /** 收货地址-详细. */
    String param_shippingStreet = "shippingStreet";
    /** 商品总类. */
    String param_commodityCategory = "commodityCategory";
    /** 商品名称. */
    String param_commodityName = "commodityName";
    /** 商品URL. */
    String param_commodityUrl = "commodityUrl";
    /** 商品单价. */
    String param_commodityUnitPrice = "commodityUnitPrice";
    /** 商品数量. */
    String param_commodityQty = "commodityQty";
    /** 是否预授权. */
    String param_isPreAuth = "isPreAuth";
    /** 币种. */
    String param_currencyCode = "currencyCode";
    /** 账户类型. */
    String param_accType = "accType";
    /** 账号. */
    String param_accNo = "accNo";
    /** 支付卡类型. */
    String param_payCardType = "payCardType";
    /** 发卡机构代码. */
     String param_issInsCode = "issInsCode";
    /** 持卡人信息. */
    String param_customerInfo = "customerInfo";
    /** 交易金额. */
   String param_txnAmt = "txnAmt";
    /** 余额. */
    String param_balance = "balance";
    /** 地区代码. */
     String param_districtCode = "districtCode";
    /** 附加地区代码. */
    String param_additionalDistrictCode = "additionalDistrictCode";
    /** 账单类型. */
    String param_billType = "billType";
    /** 账单号码. */
    String param_billNo = "billNo";
    /** 账单月份. */
   String param_billMonth = "billMonth";
    /** 账单查询要素. */
    String param_billQueryInfo = "billQueryInfo";
    /** 账单详情. */
   String param_billDetailInfo = "billDetailInfo";
    /** 账单金额. */
    String param_billAmt = "billAmt";
    /** 账单金额符号. */
    String param_billAmtSign = "billAmtSign";
    /** 绑定标识号. */
    String param_bindId = "bindId";
    /** 风险级别. */
    String param_riskLevel = "riskLevel";
    /** 绑定信息条数. */
    String param_bindInfoQty = "bindInfoQty";
    /** 绑定信息集. */
   String param_bindInfoList = "bindInfoList";
    /** 批次号. */
    String param_batchNo = "batchNo";
    /** 总笔数. */
    String param_totalQty = "totalQty";
    /** 总金额. */
     String param_totalAmt = "totalAmt";
    /** 文件类型. */
    String param_fileType = "fileType";
    /** 文件名称. */
     String param_fileName = "fileName";
    /** 批量文件内容. */
    String param_fileContent = "fileContent";
    /** 商户摘要. */
     String param_merNote = "merNote";
    /** 商户自定义域. */
    // public static final String param_merReserved = "merReserved";//接口变更删除
    /** 请求方保留域. */
    String param_reqReserved = "reqReserved";// 新增接口
    /** 保留域. */
    String param_reserved = "reserved";
    /** 终端号. */
    String param_termId = "termId";
    /** 终端类型. */
    String param_termType = "termType";
    /** 交互模式. */
    String param_interactMode = "interactMode";
    /** 发卡机构识别模式. */
    // public static final String param_recognitionMode = "recognitionMode";
    String param_issuerIdentifyMode = "issuerIdentifyMode";// 接口名称变更
    /** 商户端用户号. */
    String param_merUserId = "merUserId";
    /** 持卡人IP. */
    String param_customerIp = "customerIp";
    /** 查询流水号. */
     String param_queryId = "queryId";
    /** 原交易查询流水号. */
    String param_origQryId = "origQryId";
    /** 系统跟踪号. */
    String param_traceNo = "traceNo";
    /** 交易传输时间. */
     String param_traceTime = "traceTime";
    /** 清算日期. */
    String param_settleDate = "settleDate";
    /** 清算币种. */
    String param_settleCurrencyCode = "settleCurrencyCode";
    /** 清算金额. */
    String param_settleAmt = "settleAmt";
    /** 清算汇率. */
    String param_exchangeRate = "exchangeRate";
    /** 兑换日期. */
     String param_exchangeDate = "exchangeDate";
    /** 响应时间. */
    String param_respTime = "respTime";
    /** 原交易应答码. */
    String param_origRespCode = "origRespCode";
    /** 原交易应答信息. */
    String param_origRespMsg = "origRespMsg";
    /** 应答码. */
     String param_respCode = "respCode";
    /** 应答码信息. */
    String param_respMsg = "respMsg";
    // 新增四个报文字段merUserRegDt merUserEmail checkFlag activateStatus
    /** 商户端用户注册时间. */
    String param_merUserRegDt = "merUserRegDt";
    /** 商户端用户注册邮箱. */
    String param_merUserEmail = "merUserEmail";
    /** 验证标识. */
    String param_checkFlag = "checkFlag";
    /** 开通状态. */
    String param_activateStatus = "activateStatus";
    /** 加密证书ID. */
    String param_encryptCertId = "encryptCertId";
    /** 用户MAC、IMEI串号、SSID. */
    String param_userMac = "userMac";
    /** 关联交易. */
    // public static final String param_relationTxnType = "relationTxnType";
    /** 短信类型 */
    String param_smsType = "smsType";

    /** 风控信息域 */
    String param_riskCtrlInfo = "riskCtrlInfo";

    /** IC卡交易信息域 */
    String param_ICTransData = "ICTransData";

    /** VPC交易信息域 */
   String param_VPCTransData = "VPCTransData";

    /** 安全类型 */
    String param_securityType = "securityType";

    /** 银联订单号 */
    String param_tn = "tn";

    /** 分期付款手续费率 */
     String param_instalRate = "instalRate";

    /** 分期付款手续费率 */
    String param_mchntFeeSubsidy = "mchntFeeSubsidy";

    /** 签名公钥证书 */
    String param_signPubKeyCert = "signPubKeyCert";

    /** 加密公钥证书 */
    String param_encryptPubKeyCert = "encryptPubKeyCert";

    /** 证书类型 */
    String param_certType = "certType";
}
