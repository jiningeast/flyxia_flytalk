package com.flyxia.flytalk.unipayDomain.nativieApi;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.dao.CardBindingDao;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.CardBinding;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.CardBindService;
import com.flyxia.flytalk.unipayDomain.UnionPayConstants;
import com.flyxia.flytalk.unipayDomain.sdkconfig.AcpService;
import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionPaySDKConfig;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/26 16:09
 */
@RestController
@RequestMapping("/unionpay")
public class BindBankCard {
    @Autowired
    private UnionPaySDKConfig unionPaySdkConfig;

    @Autowired
    private CardBindService cardBindService;

    @Autowired
    private CardBindingDao cardBindingDao;

    @PostMapping("/card/certification")
    //银行卡绑定--> 认证
    public String bankCardCertification(@RequestParam Map params) throws BaseExceptionWithMessage {
        String merId = (String) params.get("merId");
        String orderId =   (String) params.get("orderId");

        String IDcard = (String) params.get("IDcard");
        String userName = (String) params.get("userName");
        String accNo = (String) params.get("accNo");

        if (merId==null||orderId==null|| IDcard==null||userName==null||accNo==null)throw new BaseExceptionWithMessage("参数非法!");

        Map<String, String>   data = new HashMap();
        data.put("version", UnionPayConstants.VERSION);
        data.put("encoding",   UnionPayConstants.ENCODING);
        data.put("signMethod",   unionPaySdkConfig.getSignMethod());
        data.put("txnType",   "72");
        data.put("txnSubType",   "12");
        data.put("bizType",   "000803");

        data.put("productId",   "B152");

        data.put("merId",   merId);

        data.put("orderId",   orderId);
        data.put("txnTime",   new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        Map<String,String>   customerInfoMap = new HashMap<String,String>();
        customerInfoMap.put("certifTp",   "01");                                                    //证件类型
        customerInfoMap.put("certifId",   IDcard);
        customerInfoMap.put("customerNm",   userName);


        String accNoEn =   AcpService.encryptData(accNo,   "UTF-8");  //这里测试的时候使用的是测试卡号，正式环境请使用真实卡号

        data.put("encryptCertId",AcpService.getEncryptCertId());
        data.put("accNo",   accNoEn);
        String customerInfoStr =   AcpService.getCustomerInfoWithEncrypt(customerInfoMap,accNo,UnionPayConstants.ENCODING);
        data.put("customerInfo",   customerInfoStr);
        Map<String, String>   reqData = AcpService.sign(data,UnionPayConstants.ENCODING);                           //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url =   unionPaySdkConfig.getSingleQueryUrl();                                                                 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
        Map<String, String>   rspData = AcpService.post(reqData,url,UnionPayConstants.ENCODING);  //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        System.out.println(rspData);
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData,   UnionPayConstants.ENCODING)){
                if(("00").equals(rspData.get("respCode"))){
                   String out_trade_no= rspData.get("orderId");
                   CardBinding cb= cardBindingDao.findOne(out_trade_no);
                   if(cb==null) throw new BaseExceptionWithMessage("订单错误!绑定失败。");
                   cb.setState("yes");
                   cardBindingDao.save(cb);
                    return JSON.toJSONString(new Result(true,"银行卡绑定成功!"));
                }
            }else{
            }
        }else{
        }
        return JSON.toJSONString(new Result(false,"银行卡认证失败!"));
    }

    @PostMapping("/card/query")
    //银行卡信息查询
    public String bankCardQury(@RequestParam Map params){
        String merId = (String) params.get("merId");
        String orderId =   (String) params.get("orderId");
        String txnTime =   (String) params.get("txnTime");

        String accNo = (String) params.get("accNo");;

        //todo: 做参数合法性过滤

        Map<String, String>   data = new HashMap();


        data.put("version", UnionPayConstants.VERSION);
        data.put("encoding",   UnionPayConstants.ENCODING);
        data.put("signMethod",   unionPaySdkConfig.getSignMethod());
        data.put("txnType",   "70");
        data.put("txnSubType",   "03");
        data.put("bizType",   "000803");

        data.put("productId",   "Q001");



        data.put("merId",   merId);


        /***要调通交易以下字段必须修改***/
        data.put("orderId",   orderId);
        data.put("txnTime",   new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        String accNoEn =   AcpService.encryptData(accNo,   "UTF-8");

        data.put("accNo",   accNoEn);
        data.put("encryptCertId",AcpService.getEncryptCertId());


        Map<String, String>   reqData = AcpService.sign(data,UnionPayConstants.ENCODING);                           //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url =   unionPaySdkConfig.getSingleQueryUrl();                                                                 //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.singleQueryUrl
        Map<String, String>   rspData = AcpService.post(reqData,url,UnionPayConstants.ENCODING);

        System.out.println(reqData);
        System.out.println(rspData);
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData,   UnionPayConstants.ENCODING)){
                if(("00").equals(rspData.get("respCode"))){//如果查询交易成功
                    return JSON.toJSONString(new Result(true,rspData.get("issueName")));
                }else{
                    return JSON.toJSONString(new Result(false,rspData.get("respMsg")));
                }
            }else{
                System.out.println("验签失败!");
            }
        }else{
            System.out.println("通信失败!");

        }

        return JSON.toJSONString(new Result(false,"查询失败!"));
    }

    @PostMapping("/cardbind")
    public String bindCard( String userId, String IDcard,String bankCard,String userName) throws BaseExceptionWithMessage {
        if (userId==null||IDcard==null||bankCard==null||userName==null)throw new BaseExceptionWithMessage("请求参数非法!");

        CardBinding cardBinding = new CardBinding();
        User user = new User(); user.setId(userId);

        cardBinding.setUserName(userName);
        cardBinding.setUser( user);
        cardBinding.setBankCardNumber(bankCard);
        cardBinding.setIdCard(IDcard);
        //一个用户可能绑定多张卡
        cardBinding.setState("no");
        SimpleBaseDto dto= (SimpleBaseDto) cardBindService.binding(cardBinding);
        if (dto.getTarget()!=null){
            Map<String,String> params = new HashMap<>();
            params.put("merId",UnionPayConstants.MERCHAT_ID);
            params.put("orderId",((CardBinding)dto.getTarget()).getId());
            params.put("IDcard",IDcard);
            params.put("userName",userName);
            params.put("accNo",((CardBinding)dto.getTarget()).getBankCardNumber());
            return bankCardCertification(params);
        }else {
            return JSON.toJSONString(new Result(false,"绑卡失败!"));
        }
    }

    @PostMapping("/cardbind/query")
    public String bindCard(String userId) throws BaseExceptionWithMessage {
        if (userId==null){
            throw new BaseExceptionWithMessage("参数错误!");
        }

        CardBinding cardBinding = new CardBinding();
        User user = new User(); user.setId(userId);
        cardBinding.setUser( user);

        SimpleBaseDto dto= (SimpleBaseDto) cardBindService.bindingQuery(cardBinding);
        if (dto.getTarget()!=null){
            return JSON.toJSONString(new Result(true,dto.getTarget()));
        }else {
            return JSON.toJSONString(new Result(false,dto.getTarget()));
        }
    }


}
