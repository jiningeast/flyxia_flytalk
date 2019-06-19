package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.handler.exception.BaseException;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;

/**
 * @author automannn@163.com
 * @time 2019/5/3 9:53
 */
public interface SendPacketService {
    BaseDto sendPacket(String token, String secretString, String userId) throws BaseExceptionWithMessage, BaseException;

    //废弃，相关逻辑已在quartz中处理
    BaseDto withdrawExpiredPacket(String token) throws BaseExceptionWithMessage;

    //废弃，相关逻辑已在quartz中处理
    BaseDto updatePacketStat(String token, String code) throws BaseExceptionWithMessage;

}
