package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.constants.GlobleConfigConstants;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.RechargeService;
import com.flyxia.flytalk.service.api.WithDrawService;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author automannn@163.com
 * @time 2019/5/3 13:55
 */
//@RestController
//@RequestMapping("/account")
    //废弃，原因: 不够安全
public class WithdrawBillController {
    @Autowired
    private WithDrawService withDrawService;



    //提现
    @PostMapping("/withdraw")
    public String reCharge(double amount,String insCode) throws BaseExceptionWithMessage {
        if (amount<=0) return JSON.toJSONString(new Result(false,"提现金额非法!"));
        if (insCode==null) return JSON.toJSONString(new Result(false,"接口调用非法!"));
        SimpleBaseDto sbd = (SimpleBaseDto) withDrawService.withDraw(amount,insCode);
        String digest = "创建";
        return unicLogic(sbd,digest);
    }

    //充值状态更新
    @PostMapping("/billupdate")
    public String updateState(String id,String code) throws BaseExceptionWithMessage {
        if (code==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        SimpleBaseDto sbd= (SimpleBaseDto) withDrawService.stateUpdate(id,code);
        String digest = "更新";
        return unicLogic(sbd,digest);
    }

    private String unicLogic(SimpleBaseDto sbd,String digest){
        if (sbd.getTarget()!=null){
            if (GlobleConfigConstants.WITHDRAW_RETURN_OBJECT) {
                return JSON.toJSONString(new Result(true,sbd.getTarget()));
            }
            return JSON.toJSONString(new Result(true,"提现订单"+digest+"成功!"));
        }
        return JSON.toJSONString(new Result(false,"提现订单"+digest+"失败!"));
    }
}
