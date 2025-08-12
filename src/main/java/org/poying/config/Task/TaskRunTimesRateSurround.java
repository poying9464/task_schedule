package org.poying.config.Task;

import org.poying.base.annotations.RunOrder;
import org.poying.base.ext.Surround;
import org.quartz.JobExecutionContext;

/**
 * 任务运行次数,成功率计算
 *
 * @author poying
 */
@RunOrder(after = 1, before = 1)
public class TaskRunTimesRateSurround implements Surround {
    @Override
    public void before(JobExecutionContext context) {

    }

    @Override
    public void after(JobExecutionContext context) {

    }

    @Override
    public void integration(JobExecutionContext context) {

    }
}
