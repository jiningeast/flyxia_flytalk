package com.flyxia.flytalk.redPacketDomain;

/**
 * @author automannn@163.com
 * @time 2019/5/2 11:29
 */
public class RedPacketHolder {
    private RedPacketAlgorithm redPacketAlgorithm;
    private int personCount;
//    private ExecutorService pool;
    private int rest_times;

    public RedPacketHolder(RedPacketAlgorithm redPacketAlgorithm, int personCount){
        this.redPacketAlgorithm = redPacketAlgorithm;
        this.personCount = personCount;
        rest_times = personCount;
//        pool=Executors.newFixedThreadPool(personCount);

    }

    public double doAssign() {
        //线程间，以地址传递信息
        final double[] result = {0};
        Thread t;
        if (rest_times>0){
            t= new Thread(new Runnable() {
                public void run() {
                    double m = redPacketAlgorithm.assign();
                    if(m>0){
                        System.out.println(Thread.currentThread().getName()+"person"+rest_times+"抢到:"+m);
                        result[0] =m;
                        rest_times--;
                    }else{
                        System.out.println(Thread.currentThread().getName()+"person"+rest_times+"没抢到红包");
                    }
                }
            });
            t.start();
            //线程同步
            synchronized (t){
                try {
                    t.wait();
                } catch (InterruptedException e) {}
            }
        }
        return result[0]==0?0.0:result[0];
    }


    //验证安全性
//    private void test(){
////        double amount = 0.0;
//    final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
////        Iterator<Double> it = total.iterator();
////        while (it.hasNext()){amount += it.next();}
////        amount = new BigDecimal(amount).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
////
////        //如 分配的总金额 与 传入的总金额不等
////        if (amount != hbAmount){
////            System.out.println("amount:"+amount);
////        }
//    }


}
