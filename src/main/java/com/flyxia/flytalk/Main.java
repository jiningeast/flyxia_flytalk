package com.flyxia.flytalk;

import com.automannn.commonUtils.http.Https;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.redPacketDomain.RedPacketUtil;
import com.flyxia.flytalk.wechatDomain.WechatPayConstants;

/**
 * @author automannn@163.com
 * @time 2019/5/17 14:35
 */
public class Main {
    /*用于进行便捷的测试*/
    public static void main(String[] args) {
//        String xmlS = "<xml><nonce_str>UDvmmDHdxemxJYiJ</nonce_str><out_trade_no>201905170100124475</out_trade_no><appid>wx440625ae89c4244d</appid><total_fee>888</total_fee><sign>DBC2F58A621924E62DC40D6608FC25C8</sign><trade_type>APP</trade_type><detail>飞聊币充值</detail><attach>null</attach><mch_id></mch_id><body>飞聊FlyChat-应用充值</body><spbill_create_ip>0:0:0:0:0:0:0:1</spbill_create_ip></xml>";
//
//        final String[] s = new String[1];
//        Thread t = new Thread(()->{
//            s[0] = Https.post("https://api.mch.weixin.qq.com/pay/unifiedorder").body(xmlS).request();
//        });
//        t.start();
//
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(s[0]);

//        RedPacketUtil.send("test",500,34,50);
//
//        int i=0;
//        while (i<50){
//             RedPacketUtil.snatch("test");
//            i++;
//        }

//        System.out.println("hello world");
        System.out.println(String.format("%.0f",1.23));
    }
}
