package org.poying.config.Task;

import org.poying.base.annotations.RunOrder;
import org.poying.base.ext.Surround;
import org.quartz.JobExecutionContext;
import org.springframework.util.StopWatch;

/**
 * 任务执行时间统计
 *
 * @author poying
 */
@RunOrder(after = -1, before = -1)
public class TaskConsumingTimeSurround implements Surround {

    StopWatch stopWatch = new StopWatch("TaskConsumingTimeSurround");

    @Override
    public void before(JobExecutionContext context) {
        stopWatch.start();
    }

    @Override
    public void after(JobExecutionContext context) {
        stopWatch.stop();
    }

    @Override
    public void integration(JobExecutionContext context) {
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
    }
}
