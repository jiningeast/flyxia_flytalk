package com.flyxia.flytalk.redPacketDomain;

import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/6 17:32
 */
public class RedPacketUtil {

    private static final Map<String,RedPacketHolder> redPacketHolderMap = new HashMap<String, RedPacketHolder>();

    public static void send(String token,double amount,int count,int personNumber){
        RedPacketAlgorithmWithCAS redPacketAlgorithm = new RedPacketAlgorithmWithCAS(amount,count);
        redPacketHolderMap.put(token,new RedPacketHolder(redPacketAlgorithm,personNumber));
    }

    public static double snatch(String token){
        double money= redPacketHolderMap.get(token).doAssign();
        return  BigDecimal.valueOf(money).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
