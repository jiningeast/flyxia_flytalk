package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;

/**
 * @author automannn@163.com
 * @time 2019/5/3 9:50
 * 提现
 */
public interface WithDrawService {

    BaseDto withDraw(double amount, String insCode) throws BaseExceptionWithMessage;

    BaseDto stateUpdate(String id, String code) throws BaseExceptionWithMessage;
}
