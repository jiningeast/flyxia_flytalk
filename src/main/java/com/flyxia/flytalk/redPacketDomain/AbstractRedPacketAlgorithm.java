package com.flyxia.flytalk.redPacketDomain;

import java.math.BigDecimal;

/**
 * @author automannn@163.com
 * @time 2019/5/2 11:06
 */
public abstract class AbstractRedPacketAlgorithm implements RedPacketAlgorithm{

    //红包金额的最小值
    protected static final double MIN = 0.01;

    //红包总金额
    protected BigDecimal amount ;
    //红包个数
    protected int count;

    public AbstractRedPacketAlgorithm(double amount,int count){
        this.amount=new BigDecimal(amount);
        this.count = count;
    }

    //计算两个数之间的随机值，结果保留两位小数
    protected double getRandom(double begin,double end){
        double random = Math.random();
        double amount = random*(end-begin)+begin;

        BigDecimal bg = new BigDecimal(amount);

        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
