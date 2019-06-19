package com.flyxia.flytalk.service.impl;

import com.flyxia.flytalk.dao.BillStateTypeDao;
import com.flyxia.flytalk.dao.FinancialInstitutionTypeDao;
import com.flyxia.flytalk.dao.WithdrawBillDao;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.BillStateType;
import com.flyxia.flytalk.entity.FinancialInstitutionType;
import com.flyxia.flytalk.entity.RechargeBill;
import com.flyxia.flytalk.entity.WithDrawBill;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.service.api.WithDrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author automannn@163.com
 * @time 2019/5/3 14:00
 */
//@Service
    //废弃，原因不够安全
public class WithDrawServiceImpl implements WithDrawService {

    @Autowired
    private FinancialInstitutionTypeDao financialInstitutionTypeDao;

    @Autowired
    private BillStateTypeDao billStateTypeDao;

    @Autowired
    private WithdrawBillDao withdrawBillDao;

    @Override
    public BaseDto withDraw(double amount, String insCode) throws BaseExceptionWithMessage {
        FinancialInstitutionType f=  financialInstitutionTypeDao.findOne(insCode);
        if (f==null) throw new BaseExceptionWithMessage("该金融机构不存在!");

        WithDrawBill w = new WithDrawBill();
        w.setAmount(amount);
        w.setState("0");
        w.setTarget(f);

        withdrawBillDao.save(w);
        if (w.getId()==null)throw new BaseExceptionWithMessage("账单创建失败!");
        return new SimpleBaseDto(w);
    }

    @Override
    public BaseDto stateUpdate(String id,String code) throws BaseExceptionWithMessage {
        BillStateType b= billStateTypeDao.findOne(code);
        if (b==null) throw new BaseExceptionWithMessage("状态非法！");

        WithDrawBill w= withdrawBillDao.getOne(id);
        if (w==null) throw new BaseExceptionWithMessage("账单不存在！");
        w.setState(code);

        return new SimpleBaseDto("状态更新成功!");
    }
}
