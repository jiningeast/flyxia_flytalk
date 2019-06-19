package com.flyxia.flytalk.alipayDomain.nativeApi;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.flyxia.flytalk.alipayDomain.AlipayConstants;
import com.flyxia.flytalk.alipayDomain.AlipayService;
import com.flyxia.flytalk.alipayDomain.dto.Merchandise;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.AlipayBinding;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.BindingService;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/10 16:02
 */
@Controller
@RequestMapping("/alipay")
@CrossOrigin
public class AliPay {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private BindingService aliBindServiceImpl;

    @ResponseBody
    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public String create(@RequestParam Map<String,String> map){
        if (map.get("out_trade_no")==null||map.get("product_code")==null||map.get("total_amount")==null
                ||map.get("subject")==null || map.get("body")==null|| map.get("passback_params")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }
        Merchandise merchandise = new Merchandise();

        merchandise.setOut_trade_no(map.get("out_trade_no"));
        merchandise.setBody(map.get("body"));
        Map map1 = JSON.parseObject(map.get("extend_params"),Map.class);

        merchandise.setExtend_params(map1);
        merchandise.setPassback_params(map.get("passback_params"));
        merchandise.setSubject(map.get("subject"));
        merchandise.setProduct_code(map.get("product_code"));
        merchandise.setTotal_amount(Double.parseDouble(map.get("total_amount")));

        SimpleBaseDto dto = (SimpleBaseDto) alipayService.doPay(merchandise);

        return JSON.toJSONString(new Result(true,dto.getTarget()));

    }

    @ResponseBody
    @RequestMapping(value = "/refund",method = RequestMethod.POST)
    public String refund(@RequestParam Map<String,String> map) throws AlipayApiException, BaseExceptionWithMessage {
        if (map.get("out_trade_no")==null||map.get("payee_type")==null||map.get("payee_account")==null
                ||map.get("amount")==null || map.get("payer_show_name")==null|| map.get("payee_real_name")==null
                ||map.get("remark")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }

        SimpleBaseDto dto = (SimpleBaseDto) alipayService.doRefund(map);

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/refund/query",method = RequestMethod.POST)
    public String refundQuery(@RequestParam Map<String,String> map) throws AlipayApiException, BaseExceptionWithMessage {
        if (map.get("out_trade_no")==null&&map.get("order_id")==null){
            return JSON.toJSONString(new Result(false,"参数非法!"));
        }

        SimpleBaseDto dto = (SimpleBaseDto) alipayService.doRefundQuery(map);

        return JSON.toJSONString(new Result(true,dto.getTarget()));
    }

    @ResponseBody
    @RequestMapping(value = "/bind",method = RequestMethod.POST)
    public String bind(@RequestParam Map<String,String> map) throws BaseExceptionWithMessage {
        if (map.get("account")==null|| map.get("real_name")==null||map.get("userid")==null) throw new BaseExceptionWithMessage("参数非法");

        User u = new User(); u.setId(map.get("userid"));
        AlipayBinding ali = new AlipayBinding();
        ali.setUser(u);
        ali.setAccount(map.get("account"));
        ali.setRealName(map.get("real_name"));
        SimpleBaseDto dto = (SimpleBaseDto) aliBindServiceImpl.binding(ali);
        if (dto.getTarget()!=null){
            return JSON.toJSONString(new Result(true,"绑卡成功!"));
        }else {
            return JSON.toJSONString(new Result(false,"绑卡失败!"));
        }
    }

    //请求进行授权
    @GetMapping("/getauth")
    @ResponseBody
    public String getinfo() throws BaseExceptionWithMessage, UnsupportedEncodingException {
        //todo:将这个uri 返回给前端，前端deeplink 调到支付宝，具体样式为：
        String auth_uri ="alipays://platformapi/startapp?appId="+ AlipayConstants.APP_ID+"&url=https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id="+ AlipayConstants.APP_ID +"&scope=auth_base&redirect_uri="+URLDecoder.decode(AlipayConstants.AUTH_BACK_URL,"utf-8");
       return  auth_uri;
    }


}
