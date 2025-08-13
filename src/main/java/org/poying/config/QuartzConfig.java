package org.poying.config;

import org.poying.config.Task.TaskResourcesSurround;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TaskResourcesSurround taskResourcesSurround;

    /**
     * 配置JobFactory，使Quartz能够使用Spring的依赖注入
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
        private final AutowireCapableBeanFactory beanFactory;

        public AutowiringSpringBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
            this.beanFactory = beanFactory;
        }

        @Override
        protected Object createJobInstance(@NonNull TriggerFiredBundle bundle) throws Exception {
            Object jobInstance = super.createJobInstance(bundle);
            beanFactory.autowireBean(jobInstance);
            return jobInstance;
        }
    }

    /**
     * 配置SchedulerFactoryBean
     *
     * @param dataSource 数据源
     * @return SchedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory(
                applicationContext.getAutowireCapableBeanFactory());
        factory.setJobFactory(jobFactory);
        factory.setDataSource(dataSource);

        // 设置Quartz属性
        Properties properties = new Properties();
        properties.put("org.quartz.scheduler.instanceName", "TaskScheduleScheduler");
        properties.put("org.quartz.scheduler.instanceId", "AUTO");

        // 线程池配置
        properties.put("org.quartz.threadPool.threadCount", "10");

        // JobStore配置
        properties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.put("org.quartz.jobStore.isClustered", "false");

        factory.setQuartzProperties(properties);
        factory.setApplicationContext(applicationContext);

        // 注册任务监控监听器
        factory.setGlobalJobListeners(taskResourcesSurround);

        return factory;
    }
}