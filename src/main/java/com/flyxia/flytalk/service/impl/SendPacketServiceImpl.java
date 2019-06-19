package com.flyxia.flytalk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.automannn.commonUtils.security.Base64;
import com.automannn.commonUtils.security.MD5;
import com.automannn.commonUtils.security.RSA;
import com.flyxia.flytalk.dao.*;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.*;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.quartzDomain.QuartzUtil;
import com.flyxia.flytalk.redPacketDomain.RedPacketUtil;
import com.flyxia.flytalk.redisDomain.JedisUtil;
import com.flyxia.flytalk.service.api.SendPacketService;
import com.flyxia.flytalk.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author automannn@163.com
 * @time 2019/5/6 11:12
 */
@Service
public class SendPacketServiceImpl implements SendPacketService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @Autowired
    private RedpacketSendBillDao redpacketSendBillDao;

    @Autowired
    private BillStateTypeDao billStateTypeDao;

    @Autowired
    private RechargeBillDao rechargeBillDao;

    @Autowired
    private WithdrawBillDao withdrawBillDao;

    @Autowired
   private JedisUtil jedisUtil;

    @Autowired
    private QuartzUtil quartzUtil;

    @Autowired
    private UserService userService;


    @Override
    @Transactional
    public BaseDto sendPacket(String token, String secretString, String userId) throws BaseExceptionWithMessage, BaseException {
        //准确性验证已在controller做了
        UserSecurity us = userSecurityDao.getOne(userId);
        if (us==null) throw new BaseExceptionWithMessage("用户信息不存在!");

        byte[] bs = RSA.decryptByPrivateKey(Base64.decode(secretString),us.getPrivacyKey());
        java.lang.String  t=  new String(bs);


//        String theMD5= MD5.generate(new String(bs),false);
//        if (!theMD5.equals(token)) throw new BaseExceptionWithMessage("传输的信息被篡改！");


        String s=new String(bs);

        //去除时间戳
        String objString = s.substring(0,s.lastIndexOf('}')+1);

        JSONObject jb = JSON.parseObject(objString);

        Double amount = jb.getDouble("amount");
        Integer count = jb.getInteger("count");
        Integer personCount = jb.getInteger("personCount");
        Integer expireTime = jb.getInteger("expireTime");



        if (amount ==null||amount<=0) throw new BaseExceptionWithMessage("红包金额非法!");
        if (count==null||count<=0) throw new BaseExceptionWithMessage("红包个数非法!");
        if (personCount==null||personCount<=0||personCount<count) throw new BaseExceptionWithMessage("抢红包人数非法!");
        if (expireTime==null||expireTime<=0)throw new BaseExceptionWithMessage("过期时长非法!");

        User u = new User();
        u.setId(userId);

        SimpleBaseDto dto = (SimpleBaseDto) userService.queryBalance(u);
         Object args= dto.getTarget();
         double userBalance= Double.valueOf((String) args) ;
        if (userBalance<amount) throw new BaseExceptionWithMessage("用户余额不足!");

        //todo,减去用户余额(即创建一个类型为发红包的 提现帐单，状态为 completed)
        WithDrawBill withdrawBill= new WithDrawBill();
        withdrawBill.setState("completed");
        withdrawBill.setAmount(amount);
        FinancialInstitutionType type=new FinancialInstitutionType();
        type.setCode("send_packet");
        withdrawBill.setTarget(type);

        withdrawBill.setUser(u);
        withdrawBillDao.save(withdrawBill);
        if (withdrawBill.getId()==null) throw new BaseExceptionWithMessage("发送红包失败！");

        RedpacketSendBill rsb= JSON.parseObject(objString, RedpacketSendBill.class);

        rsb.setToken(token);


        rsb.setUser(u);
        rsb.setPersonNumber(personCount);

        rsb.setState("yes");
        redpacketSendBillDao.save(rsb);
        if (rsb.getId()!=null){
            //红包创建成功，这里需要开启一个quartz,计算是否过期，过期则回收。
            //回收即是修改其状态为不可获取。
            //todo,开启一个任务调度。  它的终结条件为： 1.时间过期，并且仍有余额；  2.无余额
            //具体逻辑可以是： 定时执行任务，该任务先检查是否有余额，有则原路返回，同时清理map以及redis数据，无则关闭该线程，同时清除map数据
            quartzUtil.addJob(token,token+"t",rsb.getExpireTime()*1000,jedisUtil,redpacketSendBillDao,rechargeBillDao);

            //todo： 开启一个池，所有的资源从中获取，并辅助完成抢红包的逻辑
            if (jedisUtil.hasKey(token)) {
                    throw new BaseExceptionWithMessage("服务器内部错误!");
            } else {
                    jedisUtil.set(token, JSON.toJSONString(rsb));
                    RedPacketUtil.send(token, rsb.getAmount(), rsb.getCount(), rsb.getPersonNumber());
            }
                return new SimpleBaseDto(rsb);
        }else {
            throw new BaseExceptionWithMessage("红包发送失败!");
        }

    }

    @Override
    public BaseDto withdrawExpiredPacket(String token) throws BaseExceptionWithMessage {
        //todo,处理回收余额逻辑
        RedpacketSendBill rsb = new RedpacketSendBill();
        rsb.setToken(token);

        Example em = Example.of(rsb);
        RedpacketSendBill rsbl= redpacketSendBillDao.findOne(em);
        if (rsbl==null) throw new BaseExceptionWithMessage("接口调用错误!");
        User u = rsbl.getUser();


        RechargeBill rec = new RechargeBill();
        rec.setState("success");
        //todo: 计算抢红包剩余的金额
        double amount = 0;
        rec.setAmount(amount);

        FinancialInstitutionType type = new FinancialInstitutionType();
//        type.setCode("500");
        type.setCode("refund");
        rec.setTarget(type);

        rec.setUser(u);

        rechargeBillDao.save(rec);

        if (rec.getId()==null){
            throw new BaseExceptionWithMessage("退款失败!");
            //todo: 重新发起退款
        }else
        //余额增加

        //todo: 发起一个通知，通知用户，退款成功
        return new SimpleBaseDto("余额更改成功!");
    }

    @Override
    public BaseDto updatePacketStat(String token, String code) throws BaseExceptionWithMessage {
        if (billStateTypeDao.getOne(code)==null){
            throw new BaseExceptionWithMessage("状态非法！");
        };
        RedpacketSendBill rsb = new RedpacketSendBill();
        rsb.setToken(token);

        Example em = Example.of(rsb);
        RedpacketSendBill rsbl= redpacketSendBillDao.findOne(em);
        if (rsbl==null) throw new BaseExceptionWithMessage("该账单不存在!");

        rsbl.setState(code);

        return new SimpleBaseDto("更新成功!");
    }
}
