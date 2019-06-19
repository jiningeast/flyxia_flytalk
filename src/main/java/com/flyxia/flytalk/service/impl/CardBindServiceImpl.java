package com.flyxia.flytalk.service.impl;

import com.flyxia.flytalk.dao.CardBindingDao;
import com.flyxia.flytalk.dto.BaseDto;
import com.flyxia.flytalk.dto.SimpleBaseDto;
import com.flyxia.flytalk.entity.CardBinding;
import com.flyxia.flytalk.service.api.CardBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author automannn@163.com
 * @time 2019/5/24 9:06
 */
@Service
public class CardBindServiceImpl implements CardBindService {

    @Autowired
    private CardBindingDao cardBindingDao;

    @Override
    public BaseDto binding(CardBinding cardBinding) {
        SimpleBaseDto dto = new SimpleBaseDto();
        cardBindingDao.save(cardBinding);
        if (cardBinding.getId()!=null){
            //返回的内容可能会根据具体的情况调整
            dto.setTarget(cardBinding);
        }
        return dto;
    }

    @Override
    public BaseDto bindingQuery(CardBinding cardBinding) {
        SimpleBaseDto dto = new SimpleBaseDto();
        List list= cardBindingDao.findAll(Example.of(cardBinding));
        //注意，查不到时，会返回一个空列表
        dto.setTarget(list);

        return dto;
    }
}
