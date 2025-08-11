package org.poying.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SchedulerStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(SchedulerStartupListener.class);
    
    @Autowired
    private Scheduler scheduler;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (!scheduler.isStarted()) {
                logger.info("Starting Quartz Scheduler...");
                scheduler.start();
                logger.info("Quartz Scheduler started successfully");
            }
        } catch (SchedulerException e) {
            logger.error("Failed to start Quartz Scheduler", e);
        }
    }
}