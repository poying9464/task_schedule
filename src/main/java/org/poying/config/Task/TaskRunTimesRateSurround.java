package org.poying.config.Task;

import org.poying.base.annotations.RunOrder;
import org.poying.base.ext.Surround;
import org.poying.e.TaskResultDetail;
import org.poying.service.TaskResultDetailService;
import org.poying.util.BeanContextUtil;
import org.quartz.JobExecutionContext;

/**
 * 任务运行次数,成功率计算
 *
 * @author poying
 */
@RunOrder(after = 1, before = 1)
public class TaskRunTimesRateSurround implements Surround {

    private TaskResultDetailService taskResultDetailService = BeanContextUtil.getBean(TaskResultDetailService.class);

    @Override
    public void before(JobExecutionContext context) {

    }

    @Override
    public void after(JobExecutionContext context) {

    }

    @Override
    public void integration(JobExecutionContext context) {
        // 获取任务组和任务名
        String groupName = context.getJobDetail().getKey().getGroup();
        String jobName = context.getJobDetail().getKey().getName();
        // 获取任务类名
        String jobClassName = context.getJobDetail().getJobClass().getName();
        TaskResultDetail taskResultDetail = taskResultDetailService.findOneByGroupAndJobName(groupName, jobName);
    }
}
