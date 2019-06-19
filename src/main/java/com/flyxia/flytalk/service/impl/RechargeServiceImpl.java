package com.flyxia.flytalk.service.impl;

import com.flyxia.flytalk.dao.BillStateTypeDao;
import com.flyxia.flytalk.dao.FinancialInstitutionTypeDao;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.BillStateType;
import com.flyxia.flytalk.entity.FinancialInstitutionType;
import com.flyxia.flytalk.entity.RechargeBill;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.RechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author automannn@163.com
 * @time 2019/5/3 13:41
 */
//@Service
//废弃，原因不够安全
public class RechargeServiceImpl implements RechargeService {
    @Autowired
    private RechargeBillDao rechargeBillDao;

    @Autowired
    private FinancialInstitutionTypeDao financialInstitutionTypeDao;

    @Autowired
    private BillStateTypeDao billStateTypeDao;

    @Override
    public BaseDto recharge(double amount, String sourceType) throws BaseExceptionWithMessage {
        FinancialInstitutionType f=  financialInstitutionTypeDao.findOne(sourceType);
        if (f==null) throw new BaseExceptionWithMessage("该金融机构不存在!");

        RechargeBill rechargeBill = new RechargeBill();
        rechargeBill.setAmount(amount);
        rechargeBill.setTarget(f);
        rechargeBill.setState("0");

        rechargeBillDao.save(rechargeBill);
        if (rechargeBill.getId()==null)throw new BaseExceptionWithMessage("订单创建失败!");
        return new SimpleBaseDto(rechargeBill);
    }

    @Override
    public BaseDto stateChaged(String id,String billStateCode) throws BaseExceptionWithMessage {
        BillStateType b= billStateTypeDao.findOne(billStateCode);
        if (b==null) throw new BaseExceptionWithMessage("状态非法！");
        RechargeBill r= rechargeBillDao.findOne(id);
        if (r==null) throw new BaseExceptionWithMessage("账单不存在!");
        r.setState(billStateCode);
        return new SimpleBaseDto("状态更新成功!");
    }
}
