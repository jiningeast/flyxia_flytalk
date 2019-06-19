package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import org.springframework.stereotype.Service;

/**
 * @author automannn@163.com
 * @time 2019/5/3 15:28
 */
@Service
public interface UserSecurityService {

    BaseDto buildUserSecurity(String id, String priKey, String pubKey);
}
