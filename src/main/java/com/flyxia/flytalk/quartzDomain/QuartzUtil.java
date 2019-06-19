package com.flyxia.flytalk.quartzDomain;

import com.alibaba.fastjson.JSON;
import com.flyxia.flytalk.dao.RechargeBillDao;
import com.flyxia.flytalk.dao.RedpacketSendBillDao;
import com.flyxia.flytalk.dao.RedpacketSnatchBillDao;
import com.flyxia.flytalk.entity.FinancialInstitutionType;
import com.flyxia.flytalk.entity.RechargeBill;
import com.flyxia.flytalk.entity.RedpacketSendBill;
import com.flyxia.flytalk.entity.User;
import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import com.flyxia.flytalk.redisDomain.JedisUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.math.BigDecimal;
import java.util.Date;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;



/**
 * @author automannn@163.com
 * @time 2019/5/9 11:31
 */
public class QuartzUtil {

    private JobDetail jobDetail;

    private Trigger trigger;

    private Scheduler scheduler;



    public QuartzUtil(Scheduler scheduler){
        this.scheduler = scheduler;
    }

    public void addJob(String jName,String tName,long expire,JedisUtil jedisUtil,RedpacketSendBillDao sendDao,RechargeBillDao topupDao) throws BaseExceptionWithMessage {
        if (jName==null|| tName==null||expire<=0) throw new BaseExceptionWithMessage("参数非法!");
        setJobDetail(jName,jedisUtil,sendDao,topupDao);
        Date now = new Date();
        long expireTime=now.getTime()+expire;
        setTrigger(tName,new Date(expireTime));
        try {
            scheduler.scheduleJob(jobDetail,trigger);
        } catch (SchedulerException e) {
            throw new BaseExceptionWithMessage(e.getMessage());
        }
    }

    private void setJobDetail(String name,JedisUtil jedisUtil,RedpacketSendBillDao sendDao,RechargeBillDao topupDao){
        this.jobDetail =newJob(TheJob.class).withIdentity(name,"group1").build();
        this.jobDetail.getJobDataMap().put("jedisUtil",jedisUtil);
        this.jobDetail.getJobDataMap().put("sendDao",sendDao);
        this.jobDetail.getJobDataMap().put("topupDao",topupDao);

        //name 的名称为 token+'j'
        this.jobDetail.getJobDataMap().put("token",name.substring(0,name.length()-2));
    }

    private void setTrigger(String name, Date date){
        this.trigger = newTrigger().withIdentity(name,"group1").startAt(date)
                .withSchedule(simpleSchedule().withIntervalInHours(24).withRepeatCount(1)).build();
    }

    public static class TheJob implements Job {

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            //做检查逻辑
            JedisUtil jedisUtil = (JedisUtil) jobExecutionContext.getJobDetail().getJobDataMap().get("jedisUtil");

            String tokenkey = jobExecutionContext.getJobDetail().getKey().getName();
            String sendBillString= (String) jedisUtil.get(tokenkey);


            RedpacketSendBillDao sendDao = (RedpacketSendBillDao) jobExecutionContext.getJobDetail().getJobDataMap().get("sendDao");

            RechargeBillDao topupDao = (RechargeBillDao) jobExecutionContext.getJobDetail().getJobDataMap().get("topupDao");

            RedpacketSendBill sendBill = JSON.parseObject(sendBillString,RedpacketSendBill.class);
            if (sendBill==null) {
                //红包已经被抢完且回收
                //不做处理
                return;
            }else if (sendBill.getAmount()==0){
                //红包被抢完，未被回收 则修改发送红包记录状态
                RedpacketSendBill sb = sendDao.findOne(sendBill.getId());
                sb.setState("no");
                sendDao.save(sb);
            }else {
                //红包没有被抢完
                //1.修改发送红包记录状态  2.将redis记录删除  3.将该次红包存入用户余额  4.发送一个用户红包通知
                RedpacketSendBill ss=sendDao.findOne(sendBill.getId());
                ss.setState("no");
                sendDao.save(ss);

                jedisUtil.del(tokenkey);
                RechargeBill rechargeBill = new RechargeBill();
                //注意，此时的 bill是从redis，即红包中剩下的余额
                //注意转换金额（将小数点后两位进行处理）
                rechargeBill.setAmount(BigDecimal.valueOf(sendBill.getAmount()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

                User u= new User();
                u.setId(sendBill.getUser().getId());
                rechargeBill.setUser(u);

                FinancialInstitutionType type = new FinancialInstitutionType();
                type.setCode("refund");
                rechargeBill.setTarget(type);
                rechargeBill.setState("completed");

                topupDao.save(rechargeBill);

                //todo:发送用户退款通知
                System.out.println("通知用户:"+u.getId()+",退回红包金额:"+rechargeBill.getAmount());
            }

        }
    }
}
