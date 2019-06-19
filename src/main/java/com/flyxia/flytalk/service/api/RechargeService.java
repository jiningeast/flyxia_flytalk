package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;

/**
 * @author automannn@163.com
 * @time 2019/5/3 9:52
 * 充值
 */
public interface RechargeService {
    BaseDto recharge(double amount, String sourceType) throws BaseExceptionWithMessage;

    BaseDto stateChaged(String id, String billStateCode) throws BaseExceptionWithMessage;
}
