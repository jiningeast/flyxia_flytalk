package com.flyxia.flytalk.unipayDomain.nativieApi;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.constants.GlobleConfigConstants;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.unipayDomain.UnionPayConstants;
import com.flyxia.flytalk.unipayDomain.sdkconfig.AcpService;
import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionPaySDKConfig;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/23 11:31
 */
@Controller
@RequestMapping("/unionpay")
@ResponseBody
public class UnionPay {

    //注意，这时使用静态代理，或者容器注入，都是可以的
    @Autowired
    private UnionPaySDKConfig unionPaySdkConfig;

    @PostMapping("/create")
    public String create(@RequestParam Map params) throws IOException, BaseExceptionWithMessage {

        String merchatId= (String) params.get("merchat_id");
        String total_fee= (String) params.get("total_fee");
        String out_trade_number= (String) params.get("out_trade_no");
        String time= (String) params.get("time");

        String trade_type =(String) params.get("trade_type");

        if (merchatId==null||total_fee==null||out_trade_number==null||time==null||trade_type==null) throw new BaseExceptionWithMessage("参数非法!");

        String channelType ;

        //todo:确定好银联的渠道代码
        if (GlobleConfigConstants.PAY_CHANNEL_APP.equals(trade_type)){
            channelType = "08";
        }else if (GlobleConfigConstants.PAY_CHANNEL_WAP.equals(trade_type)){
            channelType = "06";
        }else if (GlobleConfigConstants.PAY_CHANNEL_WEB.equals(trade_type)){
            channelType = "07";
        }else {
            throw new BaseExceptionWithMessage("支付渠道错误!");
        }
        Map<String, String> contentData = new HashMap<String, String>();

        contentData.put("version", UnionPayConstants.VERSION);
        contentData.put("encoding", UnionPayConstants.ENCODING);
        contentData.put("signMethod", unionPaySdkConfig.getSignMethod());

        contentData.put("txnType", "01");              		 	//交易类型 01:消费
        contentData.put("txnSubType", "01");           		 	//交易子类 01：消费
        contentData.put("bizType", "000201");          		 	//填写000201

        contentData.put("channelType", channelType);          		 	//渠道类型 08手机

        /***商户接入参数***/
        contentData.put("merId", merchatId);   		 				//商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put("accessType", "0");            		 	//接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
        contentData.put("orderId", out_trade_number);        	 	    //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put("txnTime", time);		 		    //订单发送时间，取系统时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效

        contentData.put("accType", "01");					 	//账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
        contentData.put("txnAmt", total_fee);						//交易金额 单位为分，不能带小数点
        contentData.put("currencyCode", "156");                 //境内商户固定 156 人民币

        contentData.put("reqReserved","recharge");

        contentData.put("backUrl", UnionPayConstants.BACK_URL);

        /**对请求参数进行签名并发送http post请求，接收同步应答报文**/
        Map<String, String> reqData = AcpService.sign(contentData,UnionPayConstants.ENCODING);			 //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestAppUrl = unionPaySdkConfig.getAppRequestUrl();								 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData,requestAppUrl,UnionPayConstants.ENCODING);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, UnionPayConstants.ENCODING)){
                String respCode = rspData.get("respCode") ;
                if(("00").equals(respCode)){
                    //成功,获取tn号
                    String tn = rspData.get("tn");
                    System.out.println("返回成功:"+tn);
                    return JSON.toJSONString(new Result(true,tn));
                    //TODO
                }
            }else{

            }
        }else{
            //todo:可能因为超时而导致返回值为空，这时，必须调用账单发起查询
            Map maps = new HashMap();
            maps.put("orderId",out_trade_number);
            maps.put("txnTime",time);
            maps.put("merId",UnionPayConstants.MERCHAT_ID);
            query(maps);
            //TODO
        }
        return  JSON.toJSONString(new Result(false,"创建订单失败!"));
    }

    @PostMapping("/refund")
    public String refund(@RequestParam Map params) throws IOException, BaseExceptionWithMessage {
        String total_fee = (String) params.get("amount");
        String out_trade_number = (String) params.get("out_trade_no");
        String time = (String) params.get("time");
        String accNo = (String) params.get("accNo");
        String billType = (String) params.get("billType");
        String billNo = (String) params.get("billNo");
        String trade_type = (String) params.get("trade_type");
        if (total_fee == null || out_trade_number == null || time == null || trade_type == null
                || accNo == null || billType == null || billNo == null) throw new BaseExceptionWithMessage("参数非法!");
        String channelType;

        //todo:确定好银联的渠道代码
        if (GlobleConfigConstants.PAY_CHANNEL_APP.equals(trade_type)) {
            channelType = "08";
        } else if (GlobleConfigConstants.PAY_CHANNEL_WAP.equals(trade_type)) {
            channelType = "06";
        } else if (GlobleConfigConstants.PAY_CHANNEL_WEB.equals(trade_type)) {
            channelType = "07";
        } else {
            throw new BaseExceptionWithMessage("支付渠道错误!");
        }
        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", UnionPayConstants.VERSION);
        contentData.put("encoding", UnionPayConstants.ENCODING);
        contentData.put("signMethod", unionPaySdkConfig.getSignMethod());
        contentData.put("txnType", "11");
        contentData.put("txnSubType", "03");
        contentData.put("bizType", "000000");
        contentData.put("billType", billType);
        contentData.put("billNo", billNo);
        contentData.put("channelType", channelType);
        contentData.put("reqReserved","withdraw");
        /***商户接入参数***/
        contentData.put("merId", UnionPayConstants.MERCHAT_ID);
        contentData.put("accessType", "0");
        contentData.put("orderId", out_trade_number);
        contentData.put("txnTime", time);
        contentData.put("accType", "01");
        contentData.put("txnAmt", total_fee);
        contentData.put("currencyCode", "156");

        contentData.put("backUrl", UnionPayConstants.BACK_URL);

        contentData.put("encryptCertId", AcpService.getEncryptCertId());

        String accNoENC = AcpService.encryptData(accNo, UnionPayConstants.ENCODING);

        contentData.put("accNo", accNoENC);

        Map<String, String> reqData = AcpService.sign(contentData, UnionPayConstants.ENCODING);             //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestBackUrl = unionPaySdkConfig.getBackRequestUrl();                                 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, UnionPayConstants.ENCODING);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionPayConstants.ENCODING)) {
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {
                    //交易已受理，不代表交易已经成功。  等待接收后台通知更新，也可主动发起查询
                    String accNo2 = rspData.get("accNo");

                    System.out.println(reqData);
                    System.out.println(rspData);
                    String accNo3 = AcpService.decryptData(accNo2, "UTF-8");
                    return JSON.toJSONString(new Result(true, "向" + accNo3 + "转账成功!"));
                }
            } else {
            }
        } else {
            //todo:可能因为超时而导致返回值为空，这时，必须调用账单发起查询
            Map maps = new HashMap();
            maps.put("orderId",out_trade_number);
            maps.put("txnTime",time);
            maps.put("merId",UnionPayConstants.MERCHAT_ID);
            query(maps);
            //TODO
        }
        return JSON.toJSONString(new Result(false, "创建订单失败!"));
    }

    @PostMapping("/sign_contract")
    //建立委托(签约)    ---暂未测试
    public String sign_contract(@RequestParam Map params) throws IOException, BaseExceptionWithMessage {
        String merchatId = (String) params.get("merchant_id");
        String out_trade_number = (String) params.get("out_trade_no");
        String time = (String) params.get("time");
        //代收款项类型
        String billType = (String) params.get("bill_type");
        //用户号码
        String billNo = (String) params.get("bill_no");

        String trade_type = (String) params.get("trade_type");

        //(代扣款账户) 商户银行卡号
        String accNo = (String) params.get("accNo");

        //(代扣款账户) 证件类型
        String certifTp = (String) params.get("certifTp");

        //(代扣款账户) 证件号
        String certifId = (String) params.get("certifId");

        //(代扣款账户) 客户号码
        String customerNm = (String) params.get("customerNm");

        String phoneNo = (String) params.get("phoneNo");


        String validMonth = (String) params.get("validMonth");

        String frequency = (String) params.get("frequency");

        String smsCode = (String) params.get("smsCode");

        if (merchatId == null || out_trade_number == null || time == null || accNo == null || certifTp == null
                || certifId == null || customerNm == null || phoneNo == null || validMonth == null ||
                frequency == null
                || billType == null || billNo == null) throw new BaseExceptionWithMessage("参数非法!");


        String channelType;

        //todo:确定好银联的渠道代码
        if (GlobleConfigConstants.PAY_CHANNEL_APP.equals(trade_type)) {
            channelType = "08";
        } else if (GlobleConfigConstants.PAY_CHANNEL_WAP.equals(trade_type)) {
            channelType = "06";
        } else if (GlobleConfigConstants.PAY_CHANNEL_WEB.equals(trade_type)) {
            channelType = "07";
        } else {
            throw new BaseExceptionWithMessage("支付渠道错误!");
        }

        Map<String, String> contentData = new HashMap();

        contentData.put("version", UnionPayConstants.VERSION);
        contentData.put("encoding", UnionPayConstants.ENCODING);
        contentData.put("signMethod", unionPaySdkConfig.getSignMethod());//签名方法 目前只支持01-RSA方式证书加密

        contentData.put("txnType", "72");
        contentData.put("txnSubType", "16");
        contentData.put("bizType", "000000");
        contentData.put("channelType", channelType);

        /***商户接入参数***/
        contentData.put("merId", merchatId);                        //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put("accessType", "0");                        //接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
        contentData.put("orderId", out_trade_number);                    //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));                    //订单发送时间，取系统时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
        contentData.put("billType", billType);
        contentData.put("billNo", billNo);

        contentData.put("reserved", "{validMonth=" + validMonth + "&frequency=" + frequency + "}"); //委托关系期限和代收频率必填，其他选填字段请参考规范reserved字段定义

        Map<String, String> customerInfoMap = new HashMap<>();

        customerInfoMap.put("certifTp", certifTp);
        customerInfoMap.put("certifId", certifId);
        customerInfoMap.put("customerNm", customerNm);
        if (smsCode != null) customerInfoMap.put("smsCode", smsCode);

        contentData.put("encryptCertId", AcpService.getEncryptCertId());
        //需要对敏感信息进行加密
        String accNoEnc = AcpService.encryptData(accNo, "UTF-8");

        contentData.put("accNo", accNoEnc);
        customerInfoMap.put("phoneNo", phoneNo);


        String customerInfoStr = AcpService.getCustomerInfoWithEncrypt(customerInfoMap, null, UnionPayConstants.ENCODING);

        contentData.put("customerInfo", customerInfoStr);

        /**对请求参数进行签名并发送http post请求，接收同步应答报文**/
        Map<String, String> reqData = AcpService.sign(contentData, UnionPayConstants.ENCODING);             //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String requestBackUrl = unionPaySdkConfig.getBackRequestUrl();                                 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        Map<String, String> rspData = AcpService.post(reqData, requestBackUrl, UnionPayConstants.ENCODING);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionPayConstants.ENCODING)) {
                String respCode = rspData.get("respCode");
                if (("00").equals(respCode)) {

                    System.out.println(reqData);
                    System.out.println("====================");
                    System.out.println(rspData);


                    return JSON.toJSONString(new Result(true, "签约银行卡成功!"));
                } else {
                    //其他应答码为失败请排查原因或做失败处理
                    //TODO
                }
            } else {
            }
        }
        System.out.println(reqData);
        System.out.println("====================");
        System.out.println(rspData);
        return JSON.toJSONString(new Result(false, "绑定银行卡失败!"));
    }

    @PostMapping("/query")
    public void query(@RequestParam Map params) {
        String merId = (String) params.get("merId");

        String orderId = (String) params.get("orderId");

        String txnTime = (String) params.get("txnTime");


        Map<String, String> data = new HashMap<String, String>();


        data.put("version", UnionPayConstants.VERSION);

        data.put("encoding", UnionPayConstants.ENCODING);

        data.put("signMethod", unionPaySdkConfig.getSignMethod());

        data.put("txnType", "00");

        data.put("txnSubType", "00");

        data.put("bizType", "000000");


        /***商户接入参数***/

        data.put("merId", merId);

        data.put("accessType", "0");


        /***要调通交易以下字段必须修改***/

        data.put("orderId", orderId);

        data.put("txnTime", txnTime);


        Map<String, String> reqData = AcpService.sign(data, UnionPayConstants.ENCODING);

        String url = unionPaySdkConfig.getSingleQueryUrl();

        Map<String, String> rspData = AcpService.post(reqData, url, UnionPayConstants.ENCODING);


        if (!rspData.isEmpty()) {
            if (AcpService.validate(rspData, UnionPayConstants.ENCODING)) {
                if (("00").equals(rspData.get("respCode"))) {//如果查询交易成功

                    String origRespCode = rspData.get("origRespCode");

                    if (("00").equals(origRespCode)) {
                        System.out.println("查询成功!");

                    }
                }
            } else {
            }
        } else {
            //todo:这里，必须让用户重新进行查询，直到查询到结果（成功或失败）
        }

        System.out.println(reqData);
        System.out.println("==============");
        System.out.println(rspData);
    }
}