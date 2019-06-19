package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.automannn.commonUtils.security.MD5;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.SnatchPacketService;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author automannn@163.com
 * @time 2019/5/3 14:32
 */
@RestController
@RequestMapping("/account")
public class SnatchPacketController {

    @Autowired
    private SnatchPacketService snatchPacketService;

    @PostMapping("/snatchpacket")
    public String sendPacket(String token,String userId) throws Exception{

        if (token==null||userId==null) return JSON.toJSONString(new Result(false,"参数非法!"));

        SimpleBaseDto sbd = (SimpleBaseDto) snatchPacketService.snatch(token,userId);

        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(true,sbd.target));
        }else {
            return JSON.toJSONString(new Result(true,"手慢了，红包已经被抢完了！"));
        }
    }

    @PostMapping("/snatchpacket/detail")
    public String sendPacketquery(String token) throws Exception{

        if (token==null) return JSON.toJSONString(new Result(false,"参数非法!"));

        SimpleBaseDto sbd = (SimpleBaseDto) snatchPacketService.query(token);

        if (sbd.getTarget()!=null){
            return JSON.toJSONString(new Result(true,sbd.target));
        }else {
            return JSON.toJSONString(new Result(true,"查询失败!"));
        }
    }
}
