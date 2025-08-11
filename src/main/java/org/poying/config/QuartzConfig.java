package org.poying.config;

import org.poying.jobs.SampleJob;
import org.poying.jobs.FiveMinuteJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Autowired
    private TaskScheduleProperties taskScheduleProperties;

    @Bean
    public JobDetail sampleJobDetail() {
        return JobBuilder.newJob(SampleJob.class)
                .withIdentity("sampleJob")
                .usingJobData("name", "sample")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger sampleJobTrigger(JobDetail sampleJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(sampleJobDetail)
                .withIdentity("sampleJobTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();
    }
    
    @Bean
    public JobDetail fiveMinuteJobDetail() {
        return JobBuilder.newJob(FiveMinuteJob.class)
                .withIdentity("fiveMinuteJob")
                .usingJobData("name", "fiveMinute")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fiveMinuteJobTrigger(JobDetail fiveMinuteJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fiveMinuteJobDetail)
                .withIdentity("fiveMinuteJobTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
                .build();
    }
    
    @Bean
    public Scheduler scheduler() throws SchedulerException {
        // 手动创建Scheduler
        Properties props = new Properties();
        
        // Scheduler configuration
        TaskScheduleProperties.Scheduler schedulerConfig = taskScheduleProperties.getScheduler();
        props.put("org.quartz.scheduler.instanceName", schedulerConfig.getInstanceName());
        props.put("org.quartz.scheduler.instanceId", schedulerConfig.getInstanceId());

        // 线程池配置
        TaskScheduleProperties.ThreadPool threadPoolConfig = taskScheduleProperties.getThreadPool();
        props.put("org.quartz.threadPool.class", threadPoolConfig.getClazz());
        props.put("org.quartz.threadPool.threadCount", String.valueOf(threadPoolConfig.getThreadCount()));
        props.put("org.quartz.threadPool.threadPriority", String.valueOf(threadPoolConfig.getThreadPriority()));

        // JobStore配置
        TaskScheduleProperties.JobStore jobStoreConfig = taskScheduleProperties.getJobStore();
        props.put("org.quartz.jobStore.class", jobStoreConfig.getClazz());
        props.put("org.quartz.jobStore.driverDelegateClass", jobStoreConfig.getDriverDelegateClass());
        props.put("org.quartz.jobStore.tablePrefix", jobStoreConfig.getTablePrefix());
        props.put("org.quartz.jobStore.isClustered", String.valueOf(jobStoreConfig.isClustered()));
        props.put("org.quartz.jobStore.dataSource", jobStoreConfig.getDataSource());
        props.put("org.quartz.jobStore.useProperties", String.valueOf(jobStoreConfig.isUseProperties()));

        // 数据源配置
        TaskScheduleProperties.DataSource.MyDS dataSourceConfig = taskScheduleProperties.getDataSource().getMyDS();
        props.put("org.quartz.dataSource.myDS.driver", dataSourceConfig.getDriver());
        props.put("org.quartz.dataSource.myDS.URL", dataSourceConfig.getURL());
        props.put("org.quartz.dataSource.myDS.user", dataSourceConfig.getUser());
        props.put("org.quartz.dataSource.myDS.password", dataSourceConfig.getPassword());
        props.put("org.quartz.dataSource.myDS.maxConnections", String.valueOf(dataSourceConfig.getMaxConnections()));
        props.put("org.quartz.dataSource.myDS.validationQuery", dataSourceConfig.getValidationQuery());

        SchedulerFactory schedulerFactory = new StdSchedulerFactory(props);
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 添加任务和触发器
        scheduler.addJob(sampleJobDetail(), true);
        scheduler.addJob(fiveMinuteJobDetail(), true);

        // 如果触发器不存在则调度触发器
        if (scheduler.getTrigger(sampleJobTrigger(sampleJobDetail()).getKey()) == null) {
            scheduler.scheduleJob(sampleJobTrigger(sampleJobDetail()));
        }

        if (scheduler.getTrigger(fiveMinuteJobTrigger(fiveMinuteJobDetail()).getKey()) == null) {
            scheduler.scheduleJob(fiveMinuteJobTrigger(fiveMinuteJobDetail()));
        }

        return scheduler;
    }
}