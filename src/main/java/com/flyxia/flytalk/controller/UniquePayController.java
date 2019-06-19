package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.alipayDomain.nativeApi.AliPay;
import com.flyxia.flytalk.constants.GlobleConfigConstants;
import com.flyxia.flytalk.dao.ProductDao;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.entity.FinancialInstitutionType;
import com.flyxia.flytalk.entity.Product;
import com.flyxia.flytalk.entity.RechargeBill;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.unipayDomain.UnionPayConstants;
import com.flyxia.flytalk.unipayDomain.nativieApi.UnionPay;
import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionPaySDKConfig;
import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionSDKConstants;
import com.flyxia.flytalk.vo.Result;
import com.flyxia.flytalk.wechatDomain.nativeApi.WechatPay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/17 13:45
 */

@Controller
@RequestMapping("/product")
public class UniquePayController {
    @Autowired
    private WechatPay wechatPay;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private RechargeBillDao rechargeBillDao;

    @Autowired
    private AliPay aliPay;

    @Autowired
    private UnionPay unionPay;


    @PostMapping("/buy")
    @ResponseBody
    public String buy(String channel,String payType,String userId,String productCode, Integer number, HttpServletRequest request) throws BaseExceptionWithMessage, IOException {
        if (!GlobleConfigConstants.PAY_CHANNEL_APP.equals(channel)) return JSON.toJSONString(new Result(false,"暂不支持除app支付以外的其他支付方式"));
        if (payType==null||userId==null||number==null||number<=0||productCode==null) {return JSON.toJSONString(new Result(false,"参数错误!")); }

        Product p = productDao.findOne(productCode);
        if (p==null) return JSON.toJSONString(new Result(false,"商品不存在"));

        if (GlobleConfigConstants.ALIPAY_TYPE.equals(payType)){
            return alipay_buy(userId,p,number,payType);
        }else if (GlobleConfigConstants.WEPAY_TYPE.equals(payType)){
            return wechat_buy(userId,p,number,request,payType);
        }else if(GlobleConfigConstants.UNIPAY_TYPE.equals(payType)){
            return union_buy(userId,p,number,payType);
        } else {
            return JSON.toJSONString(new Result(false,"支付方式错误!"));
        }

    }

    private String union_buy(String userId,Product product, Integer number, String pay_type) throws BaseExceptionWithMessage, IOException {
        //以元为单位，微信以分为单位
        RechargeBill rb = create_bill(product,number,userId,pay_type);
        if (rb.getId()==null)return JSON.toJSONString(new Result(false,"订单创建失败!"));

        Map<String,String> params = new HashMap<>();
        params.put("merchat_id", UnionPayConstants.MERCHAT_ID);
        //注意： 银联的资金以 分为基本单位
        params.put("total_fee",String.format("%.0f",rb.getAmount()*100));

        params.put("out_trade_no",rb.getOutTradeNumber());

        params.put("time",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));


        params.put("trade_type","APP");

        return unionPay.create(params);
    }

    private String wechat_buy(String userId,Product product, Integer number, HttpServletRequest request,String pay_type) throws BaseExceptionWithMessage {
        //以元为单位，微信以分为单位
        RechargeBill rb = create_bill(product,number,userId,pay_type);
        if (rb.getId()==null)return JSON.toJSONString(new Result(false,"订单创建失败!"));

        Map<String,String> params = new HashMap<>();
        params.put("body","飞聊FlyChat-应用充值");
        params.put("detail",product.getDescription());
        params.put("attach","fly_talk");
        params.put("out_trade_no",rb.getOutTradeNumber());
        //注意： 微信的资金以 分为基本单位
        params.put("total_fee",String.format("%.0f",rb.getAmount()*100));
        params.put("trade_type","APP");

        return wechatPay.create(params,request);
    }

    private String alipay_buy(String userId,Product product, Integer number, String pay_type){
        RechargeBill rb= create_bill(product,number,userId,pay_type);
        if (rb.getId()==null)return JSON.toJSONString(new Result(false,"订单创建失败!"));
        Map<String,String> params = new HashMap<>();

        params.put("out_trade_no",rb.getOutTradeNumber());
        params.put("product_code",rb.getProductCode());
        params.put("total_amount",rb.getAmount()+"");
        params.put("subject","QUICK_MSECURITY_PAY");
        params.put("body",product.getDescription());
        params.put("passback_params","{}");

        return  aliPay.create(params);
    }

    private RechargeBill create_bill(Product product,Integer number,String userId,String pay_type){
        double total_fee= product.getPrice()*number;
        String out_trade_num = new Date().getTime()+"";

        RechargeBill rb = new RechargeBill();
        rb.setState("pending");
        FinancialInstitutionType type = new FinancialInstitutionType();
        type.setCode(pay_type);
        rb.setTarget(type);

        rb.setAmount(total_fee);
        rb.setOutTradeNumber(out_trade_num);
        rb.setProductCode(product.getCode());

        User user =new User();
        user.setId(userId);
        rb.setUser(user);

        rechargeBillDao.save(rb);
        return rb;
    }
}
