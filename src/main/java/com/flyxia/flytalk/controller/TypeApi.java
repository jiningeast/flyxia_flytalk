package com.flyxia.flytalk.controller;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.dao.BillStateTypeDao;
import com.flyxia.flytalk.dao.FinancialInstitutionTypeDao;
import com.flyxia.flytalk.entity.BillStateType;
import com.flyxia.flytalk.entity.FinancialInstitutionType;
import com.flyxia.flytalk.util.ObjectUtil;
import com.flyxia.flytalk.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author automannn@163.com
 * @time 2019/5/3 10:25
 */
@RestController
@RequestMapping("/api")
public class TypeApi {

    @Autowired
    private BillStateTypeDao billStateTypeDao;

    @Autowired
    private FinancialInstitutionTypeDao financialInstitutionTypeDao;

    //添加一种金融机构
    @PostMapping("/ins/add")
    public String addInstitution(String code,String name){
        return doAdd(code,name,FinancialInstitutionType.class,financialInstitutionTypeDao);
    }
    //查询一种金融机构
    @PostMapping("/ins/queryone")
    public String queryInstitution(String code){
       return doQuery(code,financialInstitutionTypeDao);
    }
    //查询所有金融机构
    @GetMapping("/ins/queryall")
    public String queryAllInstitution(){
       List list= financialInstitutionTypeDao.findAll();
       return JSON.toJSONString(new Result(true,list));
    }


    //添加一种账单状态类型
    @PostMapping("/billtype/add")
    public String addBillType(String code,String name){
       return doAdd(code,name,BillStateType.class,billStateTypeDao);
    }
    //查询一种账单状态类型
    @PostMapping("/billtype/queryone")
    public String queryBillType(String code){
        return doQuery(code,billStateTypeDao);
    }
    //查询所有状态类型
    @GetMapping("/billtype/queryall")
    public String queryAllBillType(){
        List list= billStateTypeDao.findAll();
        return JSON.toJSONString(new Result(true,list));
    }

    private String doAdd(String code, String name,Class clazz ,CrudRepository cc){
        if (code!=null && name!=null && !"".equals(code) && !"".equals(name)){
            Map fieldMap = new HashMap();
            fieldMap.put("code",code);
            fieldMap.put("name",name);
            cc.save(ObjectUtil.build(clazz,fieldMap));
            return JSON.toJSONString(new Result(true,"添加成功！"));
        }
        return JSON.toJSONString(new Result(false,"金融机构代码和名称均不能为空！"));
    }

    private String doQuery(String code,CrudRepository cc){
        if (code==null||"".equals(code)) return JSON.toJSONString(new Result(false,"代码参数不能为空！"));
        Object o= cc.findOne(code);
        if (o==null) return JSON.toJSONString(new Result(false,"该类型不存在！"));
        else return JSON.toJSONString(new Result(true,o));
    }
}
