package com.flyxia.flytalk.wechatDomain.gateway;

import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.entity.RechargeBill;
import com.flyxia.flytalk.util.XMLutil;
import com.flyxia.flytalk.wechatDomain.util.BuildXmlRequestFactory;
import com.flyxia.flytalk.wechatDomain.util.SignUtil;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/17 12:01
 */
@Controller
@RequestMapping("/wechatpay")
public class WechatNotifyController {

    @Autowired
    private RechargeBillDao rechargeBillDao;

    @PostMapping("/notify")
    @ResponseBody
    public String notify(HttpServletRequest request, HttpServletResponse response) throws JDOMException, IOException {
        // 1.获取 xml串 并解析   2.输出xml串
        InputStream inStream = request.getInputStream();
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        String resultxml = new String(outSteam.toByteArray(), "utf-8");

        Map<String, String> params = XMLutil.doXMLParse(resultxml);
        outSteam.close();
        inStream.close();

        Map<String,String> return_data = new HashMap<String,String>();
        ArrayList arrayList = new ArrayList();
        arrayList.add(return_data);
        if (!SignUtil.isTenpaySign(params)) {
            // 支付失败
            return_data.put("return_code", "FAIL");
            return_data.put("return_msg", "return_code不正确");

        } else {
            System.out.println("===============付款成功==============");

            //--------------------dfsf------------
            String appid = params.get("appid");
            //商户号
            String mch_id = params.get("mch_id");
            String result_code = params.get("result_code");
            String openId = params.get("openid");
            //交易类型
            String trade_type = params.get("trade_type");
            //付款银行
            String bank_type = params.get("bank_type");
            // 总金额
            String money = params.get("total_fee");
            //现金支付金额
            String cash_fee = params.get("cash_fee");
            // 微信支付订单号
            String transactionId = params.get("transaction_id");
            // // 商户订单号
            String out_trade_no = params.get("out_trade_no");
            // // 支付完成时间，格式为yyyyMMddHHmmss
            // String time_end = params.get("time_end");
            ///////////////////////////// 以下是附加参数///////////////////////////////////
            String type = params.get("attach");
            String fee_type = params.get("fee_type");
            String is_subscribe = params.get("is_subscribe");
            String err_code = params.get("err_code");
            String err_code_des = params.get("err_code_des");

            //----------------dffd----------

            RechargeBill rb = new RechargeBill();
            rb.setOutTradeNumber(out_trade_no);

            RechargeBill rb1= rechargeBillDao.findOne(Example.of(rb));
            if (rb1!=null&&rb1.getAmount()==Double.parseDouble(cash_fee)/100) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 如果有做过处理，不执行商户的业务程序

                // 注意：
                // 付款完成后，支付宝系统发送该交易状态通知
                rb1.setState("completed");
                rechargeBillDao.save(rb1);
            }

            return_data.put("return_code", "SUCCESS");
            return_data.put("return_msg", "OK");
        }
        return BuildXmlRequestFactory.load("xml",arrayList).build();



    }
}
