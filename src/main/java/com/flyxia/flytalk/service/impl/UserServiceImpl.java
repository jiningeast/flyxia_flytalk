package com.flyxia.flytalk.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.automannn.commonUtils.security.Base64;
import com.automannn.commonUtils.security.MD5;
import com.automannn.commonUtils.security.RSA;
import com.flyxia.flytalk.constants.GlobleConfigConstants;
import com.flyxia.flytalk.dao.*;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.*;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.UserService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Example;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/4/25 13:37
 */
@Service
public class UserServiceImpl implements UserService{
    private static  String basePath = "/home/flychat/upload";


    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSecurityDao userSecurityDao;

    @Autowired
    private RechargeBillDao rechargeBillDao;

    @Autowired
    private WithdrawBillDao withdrawBillDao;

    @Autowired
    private BillStateTypeDao billStateTypeDao;

    public BaseDto logOn(User u) throws BaseException {
        //todo: 合法性验证逻辑
        try {
            userDao.save(u);
        }catch (Exception e){
            throw new BaseException(302);
        }

        if (u.getId()==null) throw new BaseException(301);
        UserSecurity us = new UserSecurity();
        us.setId(u.getId());
        Map kvMap =RSA.initKey();
        us.setPrivacyKey(RSA.getPrivateKey(kvMap));
        us.setPublicKey(RSA.getPublicKey(kvMap));
        //存储用户的公私钥
        userSecurityDao.save(us);
        return new SimpleBaseDto(u);
    }

    @Override
    public BaseDto query(User u) throws BaseException {
        User user=userDao.findOne(Example.of(u));
        if(user==null){
            //todo: 查询失败的逻辑
            throw new BaseException(500);
        }else {
            return new SimpleBaseDto(user);
        }

    }

    @Override
    public BaseDto queryBalance(User u) throws BaseException {
        //1.将充值账单中与该用户有关的账单取出；   2.将提现账单中与该用户有关的账单取出
        //3.充值账单取 和 减去 提现账单取和

        Double balanceA =rechargeBillDao.getTotalFee(u.getId());
        Double balanceB =withdrawBillDao.getTotalFee(u.getId());
        if (balanceA==null) return new SimpleBaseDto(String.format("%.2f",0));
        if (balanceB==null) return new SimpleBaseDto(String.format("%.2f",balanceA));
//        BigDecimal.valueOf(balanceA).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()

        Double balance = balanceA-balanceB;
        return new SimpleBaseDto(String.format("%.2f",balance));
    }

    @Override
    public BaseDto login(User u) throws BaseException {
        User user = new User();
        user.setUsername(u.getUsername());
        user.setPhoneNumber(u.getPhoneNumber());

        //用用户名查询出来的用户
        User user2=userDao.findOne(Example.of(user));

        //用户名 + 密码查询出来的用户
        User user1=userDao.findOne(Example.of(u));

        if(user2==null){
            throw new BaseException(403);
        }else if (user1==null){
            throw new BaseException(404);
        }else {
            return new SimpleBaseDto(user1);
        }
    }

    @Override
    public BaseDto queryAll() {
        List list = userDao.findAll();
        return new SimpleBaseDto(list,list.size());
    }

    @Override
    public BaseDto amountChange(String userId,String secretString,String md5) throws BaseExceptionWithMessage {
        UserSecurity us = userSecurityDao.findOne(userId);
        if (us==null) throw new BaseExceptionWithMessage("用户信息不存在!");
        //todo: 做加解密的逻辑，以及认证的逻辑
        String priKey=  us.getPrivacyKey();
        byte[] data= RSA.decryptByPrivateKey(Base64.decode(secretString),priKey);

        String securyMd5= MD5.generate(new String(data),true);
        if (!securyMd5.equals(md5) ) throw new BaseExceptionWithMessage("传输的信息被篡改!");

        //todo: 验证参数的合法性
        JSONObject jo = JSON.parseObject(new String(data));
        if (jo.getDouble("amount")==null||jo.getString("target")==null)
            throw new BaseExceptionWithMessage("参数非法!");
        //该接口携带一个参数 type，用于识别意图
        String type=null;
        if (jo.getString("type")==null) {
            throw new BaseExceptionWithMessage("参数非法!");
        }else {
            type = jo.getString("type");
        }
        RechargeBill rb=null;
        WithDrawBill wdb=null;
        User u =new User();
        u.setId(userId);

        if (type.equals(GlobleConfigConstants.TOPUP_TYPE)){
            rb = new RechargeBill();
            rb.setUser(u);
            FinancialInstitutionType ttype = new FinancialInstitutionType();
            ttype.setCode(jo.getString("target"));
            rb.setTarget(ttype);
            rb.setAmount(jo.getDouble("amount"));
            rb.setState("pending");

        } else  if (type.equals(GlobleConfigConstants.WITH_DRAW_TYPE)){
            wdb = new WithDrawBill();
            wdb.setUser(u);
            FinancialInstitutionType ttype = new FinancialInstitutionType();
            ttype.setCode(jo.getString("target"));
            wdb.setTarget(ttype);
            wdb.setAmount(jo.getDouble("amount"));
            wdb.setState("pending");
        }else {
            throw new BaseExceptionWithMessage("参数非法!");
        }
        SimpleBaseDto simpleBaseDto=null;
        if (rb!=null) {
            rechargeBillDao.save(rb);
            if (rb.getId()!=null) simpleBaseDto = new SimpleBaseDto(rb);
        }

        if (wdb!=null){
            withdrawBillDao.save(wdb);
            if (wdb.getId()!=null) simpleBaseDto = new SimpleBaseDto(wdb);
        }

        return  simpleBaseDto;

    }

    //todo，支付的回调这块需要对接时完善
    @Override
    public BaseDto amountStatChange(String type,String id, String code) throws BaseExceptionWithMessage {
        if (billStateTypeDao.getOne(code)==null||type==null) throw new BaseExceptionWithMessage("参数非法");


        if (type.equals(GlobleConfigConstants.TOPUP_TYPE)){
           RechargeBill rb= rechargeBillDao.getOne(id);
           if (rb==null) throw new BaseExceptionWithMessage("账单不存在!");
           rb.setState(code);
        }else if (type.equals(GlobleConfigConstants.WITH_DRAW_TYPE)){
            WithDrawBill wdb= withdrawBillDao.getOne(id);
            if (wdb==null) throw new BaseExceptionWithMessage("账单不存在!");
            wdb.setState(code);
        }else {
            throw new BaseExceptionWithMessage("参数非法");
        }

        return new SimpleBaseDto("操作成功!");
    }

    @Override
    public BaseDto setAvatar(MultipartFile file, String userId) throws Exception {
        User u = userDao.findOne(userId);
        if (u==null) throw new BaseExceptionWithMessage("用户不存在");

        basePath = basePath+"/"+userId;
        basePath = basePath.replace("/", System.getProperty("file.separator"));
        File filePath = new File(basePath);
        try {
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String type = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf("."));
        String realName= new Date().getTime()+type;
        File localFile = new File(basePath, realName);
        file.transferTo(localFile);

        String path = "/"+userId+"/"+realName;
        u.setAvatar(path);

        //System.out.println(path);
        SimpleBaseDto dto = new SimpleBaseDto("操作成功!");
        return dto;
    }



}
