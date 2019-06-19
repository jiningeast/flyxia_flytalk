package com.flyxia.flytalk.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author automannn@163.com
 * @time 2019/4/25 13:50
 */
public class SimpleBaseDto implements BaseDto {
    //单个实体
    public Object target;
    //多个实体
    public List targetList;

    public int size;

    public SimpleBaseDto(Object target) {
        this.target = target;
    }

    public SimpleBaseDto(List targetList,int size) {
        this.targetList = targetList;
        this.size = size;
    }

    public SimpleBaseDto() {
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List getTargetList() {
        return targetList;
    }

    public void setTargetList(List targetList) {
        this.targetList = targetList;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
