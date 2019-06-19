package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.automannn.commonUtils.security.MD5;
import com.automannn.commonUtils.security.RSA;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.RedpacketSendBill;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.SendPacketService;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author automannn@163.com
 * @time 2019/5/3 14:31
 */
@RestController
@RequestMapping("/account")
public class SendPacketController {
    @Autowired
    private SendPacketService sendPacketService;

    //发红包
    //令牌 由 发送的字符json串，追加当前的实践戳，经过base64编码后 与 公钥加密后 的 md5值
    @PostMapping("/sendpacket")
    public String sendPacket(String token,String secretString,String userId) throws Exception{

        if (token==null) return JSON.toJSONString(new Result(false,"令牌为空!"));

        SimpleBaseDto sbd = (SimpleBaseDto) sendPacketService.sendPacket(token,secretString,userId);

        RedpacketSendBill rsb = (RedpacketSendBill) sbd.getTarget();
        if (rsb!=null){
            return JSON.toJSONString(new Result(true,rsb.getToken()));
        }else {
            return JSON.toJSONString(new Result(true,"红包发送失败!"));
        }
    }

    //回收过期红包
    @PostMapping("/withdrawpacket")
    public String withdrawExpiredPacket(String token) throws BaseExceptionWithMessage {
        if (token ==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        SimpleBaseDto sbd = (SimpleBaseDto) sendPacketService.withdrawExpiredPacket(token);
        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(true,"红包回收成功!"));
        }else {
            return JSON.toJSONString(new Result(true,"红包回收失败!"));
        }
    }

    //回收过期红包
    //todo: 状态更新的代码可能通过后台控制，不走controller流程
    @PostMapping("/packetstatupdate")
    public String packetStatUpdate(String token,String code) throws BaseExceptionWithMessage {
        if (token ==null||code==null) return JSON.toJSONString(new Result(false,"参数非法!"));
        SimpleBaseDto sbd = (SimpleBaseDto) sendPacketService.updatePacketStat(token,code);
        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(true,"状态写入成功!"));
        }else {
            return JSON.toJSONString(new Result(true,"状态写入失败!"));
        }
    }
}
