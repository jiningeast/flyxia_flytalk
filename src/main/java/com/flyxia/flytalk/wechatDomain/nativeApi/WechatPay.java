package com.flyxia.flytalk.wechatDomain.nativeApi;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.util.CusAccessObjectUtil;
import com.flyxia.flytalk.util.NonceStrUtil;
import com.flyxia.flytalk.vo.Result;
import com.flyxia.flytalk.wechatDomain.WechatPayConstants;
import com.flyxia.flytalk.wechatDomain.WechatPayService;
import com.flyxia.flytalk.wechatDomain.dto.Goods;
import com.flyxia.flytalk.wechatDomain.util.BuildXmlRequestFactory;
import com.flyxia.flytalk.wechatDomain.util.SignUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author automannn@163.com
 * @time 2019/5/13 10:02
 */
@RestController
@RequestMapping("/wechatpay")
public class WechatPay {

    @Autowired
    private WechatPayService wechatPayService;

    @ResponseBody
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String create(@RequestParam Map<String,String> map, HttpServletRequest request) throws BaseExceptionWithMessage {
        if (map.get("body")==null||map.get("detail")==null||map.get("attach")==null
                ||map.get("out_trade_no")==null || map.get("total_fee")==null|| map.get("trade_type")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }
        Goods goods = new Goods();
        goods.setBody(map.get("body"));
        goods.setDetail(map.get("detail"));
        goods.setAttach(map.get("attach"));
        goods.setOut_trade_no(map.get("out_trade_no"));
        goods.setTotal_fee(Integer.valueOf(map.get("total_fee")));
        goods.setTrade_type(map.get("trade_type"));

        String ip = CusAccessObjectUtil.getIpAddress(request);
        goods.setSpbill_create_ip(ip);
        //公共参数
        HashMap<String,String> kvMap =new HashMap<>();
        kvMap.put("appid", WechatPayConstants.APP_ID);
        kvMap.put("mch_id", WechatPayConstants.MCH_ID);
        kvMap.put("nonce_str", NonceStrUtil.getStr());
        kvMap.putAll(map);

        kvMap.put("spbill_create_ip",ip);

        kvMap.put("notify_url",WechatPayConstants.NOTIFY_URL);

        SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);
        //生成签名 注意，此时实体的属性已经全部存入 map中，因此转换xml的时候不需要带实体了
        kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));
        ArrayList list = new ArrayList();
        list.add(kvMap);

        //微信 统一支付接口的 xml串.
        String unique_string=  BuildXmlRequestFactory.load("xml",list).build();

        System.out.println(unique_string);

        //todo: 完善逻辑，等待商户号

        SimpleBaseDto dto = (SimpleBaseDto) wechatPayService.requestUniPayApi(unique_string);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("微信接口调用失败！");

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/refund_to_account",method = RequestMethod.POST)
    public String refund2Account(@RequestParam Map<String,String> map, HttpServletRequest request) throws BaseExceptionWithMessage {
        if (map.get("partner_trade_no")==null||map.get("openid")==null||map.get("check_name")==null||map.get("re_user_name")==null||
                map.get("amount")==null||map.get("desc")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }


        String ip = CusAccessObjectUtil.getIpAddress(request);

        //公共参数
        HashMap<String,String> kvMap =new HashMap<>();
        kvMap.put("appid", WechatPayConstants.APP_ID);
        kvMap.put("mch_id", WechatPayConstants.MCH_ID);
        kvMap.put("nonce_str", NonceStrUtil.getStr());

        kvMap.put("mch_appid",WechatPayConstants.MCH_APPID);

        kvMap.putAll(map);

        kvMap.put("spbill_create_ip",ip);

        SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);

        //生成签名 注意，此时实体的属性已经全部存入 map中，因此转换xml的时候不需要带实体了
        kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));
        ArrayList list = new ArrayList();
        list.add(kvMap);

        //微信 统一支付接口的 xml串.
        String unique_string=  BuildXmlRequestFactory.load("xml",list).build();

        System.out.println(unique_string);

        //todo: 完善逻辑，等待商户号,对接商户证书

        SimpleBaseDto dto = (SimpleBaseDto) wechatPayService.refund2Account(unique_string);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("微信接口调用失败！");

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/refund_to_account/query",method = RequestMethod.POST)
    public String refund2AccountQuery(@RequestParam Map<String,String> map, HttpServletRequest request) throws BaseExceptionWithMessage {
        if (map.get("partner_trade_no")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }

        //公共参数
        HashMap<String,String> kvMap =new HashMap<>();
        kvMap.put("appid", WechatPayConstants.APP_ID);
        kvMap.put("mch_id", WechatPayConstants.MCH_ID);
        kvMap.put("nonce_str", NonceStrUtil.getStr());

        kvMap.putAll(map);


        SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);

        //生成签名 注意，此时实体的属性已经全部存入 map中，因此转换xml的时候不需要带实体了
        kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));
        ArrayList list = new ArrayList();
        list.add(kvMap);

        //微信 统一支付接口的 xml串.
        String unique_string=  BuildXmlRequestFactory.load("xml",list).build();

        System.out.println(unique_string);

        //todo: 完善逻辑，等待商户号,对接商户证书

        SimpleBaseDto dto = (SimpleBaseDto) wechatPayService.refund2AccountQuery(unique_string);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("微信接口调用失败！");

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/refund_to_bank",method = RequestMethod.POST)
    public String refund2bank(@RequestParam Map<String,String> map, HttpServletRequest request) throws BaseExceptionWithMessage {
        //enc_bank_no,enc_true_name分别表示银行卡号与用户名，需用微信提供的公钥加密
        //bank_code表示 开户行编号
        if (map.get("partner_trade_no")==null||map.get("enc_bank_no")==null||map.get("enc_true_name")==null
                ||map.get("bank_code")==null || map.get("amount")==null|| map.get("desc")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }


        //公共参数
        HashMap<String,String> kvMap =new HashMap<>();
        kvMap.put("mch_id", WechatPayConstants.MCH_ID);
        kvMap.put("nonce_str", NonceStrUtil.getStr());
        kvMap.putAll(map);


        SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);

        //生成签名 注意，此时实体的属性已经全部存入 map中，因此转换xml的时候不需要带实体了
        kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));
        ArrayList list = new ArrayList();
        list.add(kvMap);

        //微信 统一支付接口的 xml串.
        String unique_string=  BuildXmlRequestFactory.load("xml",list).build();

        System.out.println(unique_string);

        //todo: 完善逻辑，等待商户号,对接商户证书，
        //todo:获取微信颁发的公钥接口,使用接口：getpubkey
        SimpleBaseDto dto = (SimpleBaseDto) wechatPayService.refund2Bank(unique_string);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("微信接口调用失败！");

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/refund_to_bank/query",method = RequestMethod.POST)
    public String refund2bankQuery(@RequestParam Map<String,String> map, HttpServletRequest request) throws BaseExceptionWithMessage {
        //enc_bank_no,enc_true_name分别表示银行卡号与用户名，需用微信提供的公钥加密
        //bank_code表示 开户行编号
        if (map.get("partner_trade_no")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }
        //公共参数
        HashMap<String,String> kvMap =new HashMap<>();
        kvMap.put("mch_id", WechatPayConstants.MCH_ID);
        kvMap.put("nonce_str", NonceStrUtil.getStr());
        kvMap.putAll(map);
        SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);
        //生成签名 注意，此时实体的属性已经全部存入 map中，因此转换xml的时候不需要带实体了
        kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));
        ArrayList list = new ArrayList();
        list.add(kvMap);
        //微信 统一支付接口的 xml串.
        String unique_string=  BuildXmlRequestFactory.load("xml",list).build();
        System.out.println(unique_string);

        //todo: 完善逻辑，等待商户号,对接商户证书
        SimpleBaseDto dto = (SimpleBaseDto) wechatPayService.refund2BankQuery(unique_string);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("微信接口调用失败！");

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/refund_to_bank/getpubkey",method = RequestMethod.POST)
    public String refund2bankGetPubkey() throws BaseExceptionWithMessage {

        //公共参数
        HashMap<String,String> kvMap =new HashMap<>();
        kvMap.put("mch_id", WechatPayConstants.MCH_ID);
        kvMap.put("nonce_str", NonceStrUtil.getStr());
        kvMap.put("sign_type","MD5");
        SortedMap<String,Object> waitingSignKV=new TreeMap<>(kvMap);
        //生成签名 注意，此时实体的属性已经全部存入 map中，因此转换xml的时候不需要带实体了
        kvMap.put("sign", SignUtil.createWxPaySign(WechatPayConstants.PRI_KEY,waitingSignKV));
        ArrayList list = new ArrayList();
        list.add(kvMap);
        //微信 统一支付接口的 xml串.
        String unique_string=  BuildXmlRequestFactory.load("xml",list).build();
        System.out.println(unique_string);

        //todo: 完善逻辑，等待商户号,对接商户证书
        SimpleBaseDto dto = (SimpleBaseDto) wechatPayService.getpubkey(unique_string);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("微信接口调用失败！");

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }
}
