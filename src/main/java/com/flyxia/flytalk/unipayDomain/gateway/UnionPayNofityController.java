package com.flyxia.flytalk.unipayDomain.gateway;

import com.flyxia.flytalk.alipayDomain.AlipayService;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.dao.WithdrawBillDao;
import com.flyxia.flytalk.entity.RechargeBill;
import com.flyxia.flytalk.entity.WithDrawBill;
import com.flyxia.flytalk.unipayDomain.sdkconfig.AcpService;
import com.flyxia.flytalk.unipayDomain.sdkconfig.UnionSDKConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/23 11:07
 */
@Controller
@RequestMapping("/unionpay")
public class UnionPayNofityController {

    @Autowired
    private RechargeBillDao rechargeBillDao;

    @Autowired
    private WithdrawBillDao withdrawBillDao;

    @PostMapping("/notify")
    public void payNotify(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("===============银联后台通知============");

        String encoding = req.getParameter(UnionSDKConstants.param_encoding);
        // 获取银联通知服务器发送的后台通知参数
        Map<String, String> reqParam = getAllRequestParam(req);

        System.out.println(reqParam);
        //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
        if (!AcpService.validate(reqParam, encoding)) {
            //验签失败，需解决验签问题
        } else {
            //todo:为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
            String orderId =reqParam.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
            String respCode = reqParam.get("respCode");
            String attach = reqParam.get("reqReserved");
            if ("withdraw".equals(attach)){
                WithDrawBill wdb = withdrawBillDao.getOne(orderId);
                if ("00".equals(respCode)){
                    wdb.setState("completed");
                    withdrawBillDao.save(wdb);
                }
            }else if ("recharge".equals(attach)){
                RechargeBill rb = new RechargeBill();
                rb.setOutTradeNumber(orderId);
                RechargeBill rb1= rechargeBillDao.findOne(Example.of(rb));
                //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
                if ("00".equals(respCode)){
                    rb1.setState("completed");
                    rechargeBillDao.save(rb1);
                }
            }
        }
        //返回给银联服务器http 200  状态码
        resp.setStatus(200);
        resp.getWriter().write("ok");
    }

    /**
     * 获取请求参数中所有的信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                //System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
                if (null == res.get(en) || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }

    /**
     * 获取请求参数中所有的信息。
     * 非struts可以改用此方法获取，好处是可以过滤掉request.getParameter方法过滤不掉的url中的参数。
     * struts可能对某些content-type会提前读取参数导致从inputstream读不到信息，所以可能用不了这个方法。理论应该可以调整struts配置使不影响，但请自己去研究。
     * 调用本方法之前不能调用req.getParameter("key");这种方法，否则会导致request取不到输入流。
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParamStream(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        try {
            String notifyStr = new String(IOUtils.toByteArray(request.getInputStream()),"utf-8");

            String[] kvs= notifyStr.split("&");
            for(String kv : kvs){
                String[] tmp = kv.split("=");
                if(tmp.length >= 2){
                    String key = tmp[0];
                    String value = URLDecoder.decode(tmp[1],"utf-8");
                    res.put(key, value);
                }
            }
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
        return res;
    }
}


