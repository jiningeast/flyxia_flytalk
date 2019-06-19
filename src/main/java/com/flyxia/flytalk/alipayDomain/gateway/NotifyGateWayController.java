package com.flyxia.flytalk.alipayDomain.gateway;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.automannn.commonUtils.security.RSA;
import com.flyxia.flytalk.alipayDomain.AlipayConstants;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.entity.RechargeBill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/10 15:03
 */
@RestController
@RequestMapping("/alipay")
public class NotifyGateWayController {

    @Autowired
    private RechargeBillDao rechargeBillDao;

    @Autowired
    private AlipayClient alipayClient;

    //授权回调地址
    @GetMapping(value = "/authCallBack")
    public void authCallback(String auth_code){
        System.out.println("1111");
        if (auth_code==null){ System.out.println("获取参数失败."); }

        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(auth_code);
        request.setGrantType("authorization_code");

        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            //todo,这里可以获取到用户的 userid,  之后的逻辑，需要在实际进行检验
            oauthTokenResponse.getUserId();
            //TODO
            System.out.println(oauthTokenResponse.getAccessToken());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/returnUrl",method = RequestMethod.GET)
    public void returnUrl(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        System.out.println("==========同步回调===========");

        Map<String,String> params = new HashMap<>();
        Map<String,String[]> requestParams =  request.getParameterMap();

        doExtractParam(params,requestParams);

        //注意，支付宝的数字签名证书存放在参数中一起传过来的。
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConstants.ALIPAY_PUB_KEY,AlipayConstants.CHARSET_TYPE,AlipayConstants.SIGN_TYPE);

        //验证通过
        if (signVerified){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            RechargeBill rb = new RechargeBill();
            rb.setOutTradeNumber(out_trade_no);

            RechargeBill rb1= rechargeBillDao.findOne(Example.of(rb));

            if (rb1!=null&&rb1.getAmount()==Double.parseDouble(total_amount)) {
                rb1.setState("completed");
                rechargeBillDao.save(rb1);
            }

            //todo:显示用户付款成功，并 提供路径  使得用户返回余额界面
            response.getWriter().write(
                    "trade_no:" + trade_no + "<br/>out_trade_no:" + out_trade_no + "<br/>total_amount:" + total_amount);
        }else {
            response.getWriter().write("验签失败");
        }
        response.getWriter().flush();
        response.getWriter().close();
    }

    @RequestMapping(value = "/notifyUrl", method = RequestMethod.POST)
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response)
            throws AlipayApiException, IOException {
        System.out.println("#################################异步回调######################################");

        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();

        doExtractParam(params,requestParams);

        //注意，支付宝的数字签名证书存放在参数中一起传过来的。
        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConstants.ALIPAY_PUB_KEY, AlipayConstants.CHARSET_TYPE, AlipayConstants.SIGN_TYPE); // 调用SDK验证签名


		/*
		 * 实际验证过程建议商户务必添加以下校验： 1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
		 * 2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）， 3、校验通知中的seller_id（或者seller_email)
		 * 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
		 * 4、验证app_id是否为该商户本身。
		 */
        if (signVerified) {// 验证成功
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");


            RechargeBill rb = new RechargeBill();
            rb.setOutTradeNumber(out_trade_no);

            RechargeBill rb1= rechargeBillDao.findOne(Example.of(rb));


            if (rb1!=null&&rb1.getAmount()==Double.parseDouble(total_amount)&&trade_status.equals("TRADE_FINISHED")) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 如果有做过处理，不执行商户的业务程序

                // 注意：
                // 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            } else if (rb1!=null&&rb1.getAmount()==Double.parseDouble(total_amount)&&trade_status.equals("TRADE_SUCCESS")) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 如果有做过处理，不执行商户的业务程序

                // 注意：
                // 付款完成后，支付宝系统发送该交易状态通知
                rb1.setState("completed");
                rechargeBillDao.save(rb1);
            }

            System.out.println("异步回调验证成功");
            response.getWriter().write("success");

        } else {// 验证失败
            System.out.println("异步回调验证失败");
            response.getWriter().write("fail");

            // 调试用，写文本函数记录程序运行情况是否正常
            // String sWord = AlipaySignature.getSignCheckContentV1(params);
            // AlipayConfig.logResult(sWord);
        }
        response.getWriter().flush();
        response.getWriter().close();
    }

    private void doExtractParam(Map params,Map requestParams){
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name =  iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            //将列表里的值转换为 字符串的方式
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        System.out.println(params);
    }
}
