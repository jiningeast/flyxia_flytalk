package com.flyxia.flytalk.redPacketDomain;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author automannn@163.com
 * @time 2019/5/2 11:13
 */
public class RedPacketAlgorithmWithCAS extends AbstractRedPacketAlgorithm {
    //红包总金额
    private AtomicReference<BigDecimal> amount = new AtomicReference<BigDecimal>();
    //红包个数
    private AtomicInteger count;

    public RedPacketAlgorithmWithCAS(double amount, int count) {
        super(amount, count);
        this.amount.set(new BigDecimal(amount));
        this.count = new AtomicInteger(count);
    }

    @Override
    public double assign() {
        return assignHongBao();
    }

    //用CAS操作随机分配一个红包
    private double assignHongBao(){
        while (true){
            //如红包个数为0，表示红包抢完，返回0
            if (this.count.get()<=0){return 0.0;}

            //如果是最后一个红包则直接将剩余的金额返回
            if (this.count.get()==1){
                BigDecimal c = this.amount.get();
                if (this.amount.compareAndSet(c,new BigDecimal(0))){
                    this.count.decrementAndGet();
                    return c.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                }else{
                    //如果最后一个红包金额修改失败，表示最后一个红包已经被抢走
                    return 0.0;
                }
            }
            BigDecimal balanceAmount = this.amount.get();
            int balanceCount = this.count.get();
            if (balanceCount ==0){ return  0.0;}

            //求出剩余红包金额和个数的平均值
            double avg = balanceAmount.divide(new BigDecimal(balanceCount),8,BigDecimal.ROUND_HALF_UP).doubleValue();
            //随机获取一个MIN 到 2倍平均值 之间的值
            double cur = getRandom(MIN,avg*2);
            //获取剩余金额和分配金额的差值
            double b = balanceAmount.add(new BigDecimal(-cur)).doubleValue();
            //获取剩余红包个数应该有的最小的红包金额
            //如MIN=0.01,这次分配后还剩两个红包，则金额至少要剩下2分钱
            double c = new BigDecimal(balanceCount-1).multiply(new BigDecimal(MIN)).doubleValue();
            //分配之后剩余金额b  需要大于等于 剩余的最小值c ， 如果不满足，则需要重新分配红包大小，直至满足为止.
            while (b<c){
                cur= getRandom(MIN,avg*2);
                b= balanceAmount.add(new BigDecimal(-cur)).doubleValue();
            }

            //如是最后一个红包，则直接将剩余的红包返回
            //返回之前再一次判断的目的是  为了在多线程情况下，如果在返回结果之前已经被抢到只剩最后一个的时候
            //还是返回随机活得金额的化，则会导致总金额不会被抢完
            if (this.count.get()==1){
                BigDecimal c1 = this.amount.get();
                if (this.amount .compareAndSet(c1,new BigDecimal(0))){
                    this.count.decrementAndGet();
                    return  c1.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                }else {
                    //若最后一个红包金额修改失败，表示最后一个红包已经被抢走，没有剩余红包了 ，直接返回0
                    return 0.0;
                }
            }

            //CAS更新金额和个数同时成功，则返回随机分配的红包金额
            if (amount.compareAndSet(balanceAmount,balanceAmount.add(new BigDecimal(-cur)))){
                this.count.decrementAndGet();
                return cur;
            }
        }
    }
}
