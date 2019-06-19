package com.flyxia.flytalk.service.impl;

import com.flyxia.flytalk.dao.AlipayBindingDao;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.AlipayBinding;
import com.flyxia.flytalk.entity.CardBinding;
import com.flyxia.flytalk.service.api.AliBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author automannn@163.com
 * @time 2019/5/27 16:13
 */
@Service
public class AliBindServiceImpl implements AliBindService {
    @Autowired
    private AlipayBindingDao dao;

    @Override
    public BaseDto binding(AlipayBinding ali) {
        dao.save(ali);
        if (ali.getId()==null) return new SimpleBaseDto();
        return new SimpleBaseDto(ali);
    }

    @Override
    public BaseDto bindingQuery(AlipayBinding ali) {
       List list= dao.findAll(Example.of(ali));
        return new SimpleBaseDto(list);
    }
}
