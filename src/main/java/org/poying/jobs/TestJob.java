package org.poying.jobs;

import org.poying.base.annotations.TaskRunnerProcessor;
import org.poying.config.Task.TaskResourcesSurround;
import org.poying.config.Task.TaskRunTimesRateSurround;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.poying.base.BaseJob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@TaskRunnerProcessor(surrounds = {TaskResourcesSurround.class,
        TaskRunTimesRateSurround.class})
public class TestJob extends BaseJob {
    
    @Override
    protected void executeJob(JobExecutionContext context) throws JobExecutionException {
        String jobId = UUID.randomUUID().toString();
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        logger.info("TestJob started - ID: {}, Time: {}", jobId, currentTime);
        
        try {
            // 模拟一些工作 - 简单的任务逻辑
            logger.info("TestJob is performing some work...");
            
            // 模拟耗时操作
            Thread.sleep(500);
            
            logger.info("TestJob completed successfully - ID: {}, Time: {}", jobId, currentTime);
        } catch (InterruptedException e) {
            logger.error("TestJob was interrupted - ID: {}", jobId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("TestJob encountered an error - ID: {}", jobId, e);
        }
    }
}