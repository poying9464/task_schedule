package org.poying.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 所有任务的基类，提供统一的日志处理功能
 * 通过MDC机制实现每个任务单独的日志文件
 */
public abstract class BaseJob implements Job {
    
    protected Logger logger;
    
    public BaseJob() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        // 从任务类名中提取任务名称，用于日志文件命名
        String jobName = this.getClass().getSimpleName();
        MDC.put("jobName", jobName);
        
        try {
            // 调用具体的任务执行逻辑
            executeJob(context);
        } finally {
            // 清理MDC上下文
            MDC.clear();
        }
    }
    
    /**
     * 具体的任务执行逻辑，由子类实现
     * 
     * @param context 任务执行上下文
     * @throws JobExecutionException 任务执行异常
     */
    protected abstract void executeJob(JobExecutionContext context) throws JobExecutionException;
}