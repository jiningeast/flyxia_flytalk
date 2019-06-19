package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.flyxia.flytalk.alipayDomain.nativeApi.AliPay;
import com.flyxia.flytalk.constants.GlobleConfigConstants;
import com.flyxia.flytalk.dao.ProductDao;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.dao.WithdrawBillDao;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.*;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.AliBindService;
import com.flyxia.flytalk.service.api.BindingService;
import com.flyxia.flytalk.service.api.UserService;
import com.flyxia.flytalk.unipayDomain.nativieApi.UnionPay;
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
import java.util.List;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/27 15:42
 */
@Controller
@RequestMapping("/account")
public class UniqueWithdrawController {
    @Autowired
    private WechatPay wechatPay;

    @Autowired
    private BindingService aliBindServiceImpl;

    @Autowired
    private BindingService cardBindServiceImpl;

    @Autowired
    private WithdrawBillDao withdrawBillDao;

    @Autowired
    private UserService userService;

    @Autowired
    private AliPay aliPay;

    @Autowired
    private UnionPay unionPay;

    @PostMapping("/withdraw")
    @ResponseBody
    public String withdraw(String userId,String total_fee, String pay_type, String pay_channel,HttpServletRequest request) throws BaseExceptionWithMessage, AlipayApiException, IOException, BaseException {
        if (total_fee==null||pay_type==null||pay_channel==null) throw new BaseExceptionWithMessage("参数非法!");

        if (Double.parseDouble(total_fee)<=0) throw new BaseExceptionWithMessage("退款金额必须大于0。");

        User u= new User();u.setId(userId);

        SimpleBaseDto dto= (SimpleBaseDto) userService.queryBalance(u);
        double balance = Double.parseDouble((String) dto.getTarget());
        if (balance<Double.parseDouble(total_fee)) throw new BaseExceptionWithMessage("余额不足!");

        WithDrawBill w1 = new WithDrawBill();
        w1.setUser(u);

        FinancialInstitutionType type = new FinancialInstitutionType();
        type.setCode(pay_type);
        w1.setTarget(type);
        w1.setAmount(Double.parseDouble(total_fee));
        w1.setState("pending");
        withdrawBillDao.save(w1);
        if (w1.getId()==null) throw new BaseExceptionWithMessage("创建订单失败!");

        if (GlobleConfigConstants.ALIPAY_TYPE.equals(pay_type)){
            return alipay_withdraw(w1,userId);
        }else if (GlobleConfigConstants.WEPAY_TYPE.equals(pay_type)){
            return wepay_withdraw(w1,userId,request);
        }else if (GlobleConfigConstants.UNIPAY_TYPE.equals(pay_type)){
            return unionpay_withdraw(w1,userId);
        }
        return JSON.toJSONString(new Result(false,"暂不支持该退款方式!"));
    }

    private String alipay_withdraw(WithDrawBill withDrawBill,String userId) throws BaseExceptionWithMessage, AlipayApiException {

        Map<String,String> params = new HashMap<>();
        params.put("out_trade_no",withDrawBill.getId());
        params.put("amount",withDrawBill.getAmount()+"");
        params.put("payer_show_name","小飞侠网络");

        User u = new User(); u.setId(userId);
        AlipayBinding bind = new AlipayBinding(); bind.setUser(u);
        SimpleBaseDto dto = (SimpleBaseDto) aliBindServiceImpl.bindingQuery(bind);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("尚未绑定支付宝账号!");

        List<AlipayBinding> lists = (List<AlipayBinding>) dto.getTarget();
        if (lists.size()==0) throw new BaseExceptionWithMessage("尚未绑定支付宝账号!");

        AlipayBinding alipayBinding = lists.get(0);
        params.put("payee_type","ALIPAY_LOGONID");
        params.put("payee_account",alipayBinding.getAccount());
        params.put("payee_real_name",alipayBinding.getRealName());
        params.put("remark","退款");
        return aliPay.refund(params);
    }

    private String wepay_withdraw(WithDrawBill withDrawBill, String userId, HttpServletRequest request) throws BaseExceptionWithMessage {
        HashMap params = new HashMap();
        //todo:OPENID  设置-->这个openid 通过调用 微信接口授权获得（在微信绑定中获取）
        //todo:re_user_name设置  同上  可为空
        params.put("partner_trade_no",withDrawBill.getId());
        params.put("check_name","NO_CHECK");
        params.put("amount",withDrawBill.getAmount()*100);
        params.put("desc","小飞侠网络-退款");
        return wechatPay.refund2Account(params,request);
    }

    private String unionpay_withdraw(WithDrawBill withDrawBill, String userId) throws BaseExceptionWithMessage, IOException {
        User u = new User();u.setId(userId);
        CardBinding bind = new CardBinding(); bind.setUser(u);

        SimpleBaseDto dto = (SimpleBaseDto) cardBindServiceImpl.bindingQuery(bind);
        if (dto.getTarget()==null) throw new BaseExceptionWithMessage("尚未绑定银行卡号!");

        List<CardBinding> lists = (List<CardBinding>) dto.getTarget();
        if (lists.size()==0) throw new BaseExceptionWithMessage("尚未绑定银行卡号!");

        CardBinding cardBinding = lists.get(0);

        HashMap params = new HashMap();
        //todo: 设置 accNo，userName,绑定银行卡的id号
        params.put("accNo",cardBinding.getBankCardNumber());
        params.put("userName",cardBinding.getUserName());
        params.put("amount",String.format("%.0f",withDrawBill.getAmount()*100));
        params.put("out_trade_no",withDrawBill.getId());
        params.put("time",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        params.put("billType","OT");
        params.put("billNo",cardBinding.getId());
        params.put("trade_type","WEB");
        return unionPay.refund(params);
    }

}
