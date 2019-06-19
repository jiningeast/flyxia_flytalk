package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;

/**
 * @author automannn@163.com
 * @time 2019/5/3 9:53
 * 抢红包
 */
public interface SnatchPacketService {
    BaseDto snatch(String token, String userId) throws BaseExceptionWithMessage;

    BaseDto query(String token) throws BaseExceptionWithMessage;
}
