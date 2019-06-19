package com.flyxia.flytalk.quartzDomain;

import com.flyxia.flytalk.handler.exception.BaseExceptionWithMessage;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import static org.quartz.JobBuilder.newJob;

/**
 * @author automannn@163.com
 * @time 2019/5/9 11:35
 */

@Configuration
public class QuartzConfig {
    @Bean
    public Scheduler scheduler() throws BaseExceptionWithMessage {
        Scheduler scheduler=null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new BaseExceptionWithMessage(e.getMessage());
        }

        return scheduler;
    }

    @Bean
    public QuartzUtil quartzUtil(Scheduler scheduler){
        QuartzUtil quartzUtil =new QuartzUtil(scheduler);
        return quartzUtil;
    }
}
