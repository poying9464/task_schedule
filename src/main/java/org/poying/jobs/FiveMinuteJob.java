package org.poying.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FiveMinuteJob extends BaseJob {
    
    @Override
    protected void executeJob(JobExecutionContext context) throws JobExecutionException {
        String jobId = UUID.randomUUID().toString();
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        logger.info("FiveMinuteJob started - ID: {}, Time: {}", jobId, currentTime);
        
        try {
            // 模拟一些工作
            Thread.sleep(200);
            
            logger.info("FiveMinuteJob completed successfully - ID: {}, Time: {}", jobId, currentTime);
        } catch (InterruptedException e) {
            logger.error("FiveMinuteJob was interrupted - ID: {}", jobId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("FiveMinuteJob encountered an error - ID: {}", jobId, e);
        }
    }
}