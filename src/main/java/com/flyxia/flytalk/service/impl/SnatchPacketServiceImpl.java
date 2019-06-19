package com.flyxia.flytalk.service.impl;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.constants.GlobleConfigConstants;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.dao.RedpacketSendBillDao;
import com.flyxia.flytalk.dao.RedpacketSnatchBillDao;
import com.flyxia.flytalk.dao.UserSecurityDao;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.*;

import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.redPacketDomain.RedPacketUtil;
import com.flyxia.flytalk.redisDomain.JedisUtil;
import com.flyxia.flytalk.service.api.SnatchPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


import java.util.List;


/**
 * @author automannn@163.com
 * @time 2019/5/6 14:40
 */
@Service
public class SnatchPacketServiceImpl implements SnatchPacketService {


    @Autowired
    private JedisUtil jedisUtil;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @Autowired
    private RedpacketSnatchBillDao redpacketSnatchBillDao;

    @Autowired
    private RedpacketSendBillDao sendBillDao;

    @Autowired
    private RechargeBillDao rechargeBillDao;


    @Override
    public BaseDto snatch(String token, String userId) throws BaseExceptionWithMessage {
       UserSecurity us =  userSecurityDao.getOne(userId);
       if (us== null) throw new BaseExceptionWithMessage("用户信息不存在!");

       //todo: 同一个红包，某个用户不能重复抢
        User u = new User();
        u.setId(userId);

        RedPacketSnatchBill rpsbill = new RedPacketSnatchBill();
        rpsbill.setToken(token);
        rpsbill.setUser(u);

        RedPacketSnatchBill snn = redpacketSnatchBillDao.findOne(Example.of(rpsbill));
        if (snn!=null) throw new BaseExceptionWithMessage("您已经领取了该红包:"+snn.getAmount()+"元!");

       //todo: 开始抢红包的逻辑
        //开启redis的写锁
        GlobleConfigConstants.lock.readLock().lock();
        String sendBillString=null;
        try {
            sendBillString = (String) jedisUtil.get(token);
            if (sendBillString==null) throw new BaseExceptionWithMessage("该红包不存在!");
        }finally {
            GlobleConfigConstants.lock.readLock().unlock();
        }

        double money= RedPacketUtil.snatch(token);
        if (money==0){
            //红包被抢完了,但是没有过期
            jedisUtil.del(token);

            //todo: 红包抢完了；  1，修改发红包状态；  2.删除redis缓存  3.提示用户
            RedpacketSendBill sendBill=new RedpacketSendBill();
            sendBill.setToken(token);
            RedpacketSendBill sendBill1= sendBillDao.findOne(Example.of(sendBill));
            sendBill1.setState("no");
            sendBillDao.save(sendBill);

            return new SimpleBaseDto("手慢了！红包已经被抢完了。");
        } else {
            //开启jedis的写锁
            GlobleConfigConstants.lock.writeLock().lock();
            try {
                    //1.将被抢的余额放入数据库；   2.更新剩余的余额   3. redis具有过期时间。   4.redis实时红包实时变化表  5.更新账户余额
                    RedPacketSnatchBill rpsb = new RedPacketSnatchBill();
                    rpsb.setAmount(money);
                    User uuu = new User();
                    uuu.setId(userId);

                    rpsb.setUser(uuu);

                    rpsb.setToken(token);
                    //存入数据库后
                    redpacketSnatchBillDao.save(rpsb);

                    if (rpsb.getId()!=null){
                        //更新用户的帐单
                        RechargeBill rb = new RechargeBill();
                        rb.setUser(u);
                        rb.setAmount(money);
                        FinancialInstitutionType type = new FinancialInstitutionType();
                        type.setCode("snatch_packet");
                        rb.setTarget(type);

                        rb.setState("completed");

                        rechargeBillDao.save(rb);

                        if (rb.getId()==null) throw new BaseExceptionWithMessage("内部错误！");
                        //更新 redis的缓存
                        RedpacketSendBill sendBill = JSON.parseObject(sendBillString,RedpacketSendBill.class);
                        sendBill.setAmount(sendBill.getAmount()-money);
                        sendBill.setCount(sendBill.getCount()-1);

                        jedisUtil.set(token,JSON.toJSONString(sendBill));
                }
            }finally {
                    GlobleConfigConstants.lock.writeLock().unlock();
                }
        };

        return new SimpleBaseDto(String.format("%.2f", money));
    }

    @Override
    public BaseDto query(String token) throws BaseExceptionWithMessage {
        RedPacketSnatchBill rsb = new RedPacketSnatchBill();

        rsb.setToken(token);
        Example example = Example.of(rsb);
        List<RedPacketSnatchBill> list =  redpacketSnatchBillDao.findAll(example);
        SimpleBaseDto simpleBaseDto = new SimpleBaseDto(list);
        return simpleBaseDto;
    }
}
