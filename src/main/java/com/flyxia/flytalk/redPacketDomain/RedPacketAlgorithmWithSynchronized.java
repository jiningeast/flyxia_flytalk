package com.flyxia.flytalk.redPacketDomain;

import java.math.BigDecimal;

/**
 * @author automannn@163.com
 * @time 2019/5/2 11:14
 */
public class RedPacketAlgorithmWithSynchronized extends AbstractRedPacketAlgorithm{
    public RedPacketAlgorithmWithSynchronized(double amount, int count) {
        super(amount, count);
    }

    @Override
    public double assign() {
        return 0;
    }

    public synchronized  double assignHongBao2(){
        if (count==0) return 0.0;

        if (count==1) {
            count --;
            return amount.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        //求出剩余红包金额 和个数的 平均值
        double avg = amount.divide(new BigDecimal(count),8,BigDecimal.ROUND_HALF_UP).doubleValue();

        //随机获取一个MIN 到2倍平均值  之间的值
        double cur = getRandom(MIN,avg*2);

        //获取剩余金额和分配金额的差值
        double b= amount.add(new BigDecimal(-cur)).doubleValue();

        //剩余红包的最小额度
        double c= new BigDecimal(count-1).multiply(new BigDecimal(MIN)).doubleValue();

        //分配之后剩余金额b  需要大于等于 剩余的最小值 c， 如不满足则需重新分配，直至满足
        while (b<c){
            cur = getRandom(MIN,avg*2);
            b= amount.add(new BigDecimal(-cur)).doubleValue();
        }
        amount = amount.add(new BigDecimal(-cur));
        count--;
        return  cur;
    }
}
