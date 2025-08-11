package org.poying.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.UUID;

public class SampleJob extends BaseJob {

    @Override
    protected void executeJob(JobExecutionContext context) throws JobExecutionException {
        String jobId = UUID.randomUUID().toString();
        logger.info("SampleJob started - ID: {}", jobId);
        
        try {
            // 模拟一些工作
            Thread.sleep(100);
            
            logger.info("SampleJob completed successfully - ID: {}", jobId);
        } catch (InterruptedException e) {
            logger.error("SampleJob was interrupted - ID: {}", jobId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.error("SampleJob encountered an error - ID: {}", jobId, e);
        }
    }
}