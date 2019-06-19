package com.flyxia.flytalk.service.api;

import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.entity.CardBinding;

/**
 * @author automannn@163.com
 * @time 2019/5/27 16:12
 */
public interface BindingService<T> {

    //绑卡
    BaseDto binding(T t);

    BaseDto bindingQuery(T t);
}
